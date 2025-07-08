package com.example.shopee.controller.client;


import com.example.shopee.config.VNPayConfig;
import com.example.shopee.entity.*;
import com.example.shopee.repository.*;
import com.example.shopee.service.EmailSenderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/client/checkout")
public class ClientCheckoutController {

    private Boolean orderSuccess;
    private String transactionNo;
    private String orderInfo;
    private String method;
    private String transactionStatus;
    private OrderEntity orderEntity = new OrderEntity();
    String address, fullName, email, phone;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private VoucherRepository voucherRepository;
    @Autowired
    private CartDetailRepository cartDetailRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    public Map<Long, Set<VoucherEntity>> getApplicableVouchersPerVendor(Set<CartDetailEntity> cartDetails) {
        LocalDateTime now = LocalDateTime.now();
        Map<Long, Set<VoucherEntity>> result = new HashMap<>();

        Set<VoucherEntity> adminVouchers = voucherRepository.findAll().stream()
                .filter(v -> v.getUserEntity() != null)
                .filter(v -> v.getUserEntity().getRoleEntities().stream().anyMatch(role -> role.getId() == 1L))
                .filter(v -> v.getAmount() != null && v.getAmount() > 0)
                .filter(v -> v.getStatus() == 1)
                .filter(v -> (v.getStartTime() == null || !v.getStartTime().isAfter(now)) &&
                        (v.getEndTime() == null || !v.getEndTime().isBefore(now)))
                .collect(Collectors.toSet());

        Set<Long> processedVendors = new HashSet<>();

        for (CartDetailEntity cd : cartDetails) {
            Long vendorId = cd.getProductEntity().getUser().getId();

            if (!processedVendors.contains(vendorId)) {
                Set<VoucherEntity> vendorVouchers = voucherRepository.findAllBySellerId(vendorId).stream()
                        .filter(v -> v.getAmount() != null && v.getAmount() > 0)
                        .filter(v -> v.getStatus() == 1)
                        .filter(v -> (v.getStartTime() == null || !v.getStartTime().isAfter(now)) &&
                                (v.getEndTime() == null || !v.getEndTime().isBefore(now)))
                        .collect(Collectors.toSet());

                Set<VoucherEntity> applicable = new HashSet<>(adminVouchers);
                applicable.addAll(vendorVouchers);

                result.put(vendorId, applicable);
                processedVendors.add(vendorId);
            }
        }

        return result;
    }


    @GetMapping("")
    public String checkout(Model model, @RequestParam("selectedItemIds") List<Long> selectedItemIds) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return "redirect:/login";
        }

        UserEntity user = optionalUser.get();

        Optional<CartEntity> cartOpt = cartRepository.findByUserEntity(user);
        if (cartOpt.isEmpty()) {
            return "redirect:/cart";
        }

        CartEntity cart = cartOpt.get();

        Set<CartDetailEntity> selectedCartDetails = cart.getCartDetailEntities().stream()
                .filter(cd -> selectedItemIds.contains(cd.getId()))
                .collect(Collectors.toSet());

        if (selectedCartDetails.isEmpty()) {
            return "redirect:/cart";
        }

        BigDecimal totalPrice = selectedCartDetails.stream()
                .map(cd -> cd.getPriceOfOne().multiply(BigDecimal.valueOf(cd.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println(totalPrice);

        Map<Long, Set<VoucherEntity>> vouchersPerVendor = getApplicableVouchersPerVendor(selectedCartDetails);
        model.addAttribute("vouchersPerVendor", vouchersPerVendor);

        Map<Long, List<CartDetailEntity>> cartDetailsPerVendor = selectedCartDetails.stream()
                .collect(Collectors.groupingBy(cd -> cd.getProductEntity().getUser().getId()));

        model.addAttribute("cartDetailsPerVendor", cartDetailsPerVendor);

        model.addAttribute("cartDetails", selectedCartDetails);
        model.addAttribute("totalPrice", totalPrice);

        return "checkout";
    }


    @PostMapping("")
    public String checkoutPage(
            Model model,
            @RequestParam("paymentMethod") String paymentMethod,
            @RequestParam Map<String, String> voucherSelections,
            @RequestParam("fullName") String fullName,
            @RequestParam("phone") String phone,
            @RequestParam("email") String email,
            @RequestParam("address") String address,
            HttpServletRequest request
    ) throws UnsupportedEncodingException {


        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(userEmail).orElseThrow();

        CartEntity cartEntity = userEntity.getCartEntity();
        Set<CartDetailEntity> cartDetails = cartEntity.getCartDetailEntities();

        if (cartEntity == null || cartEntity.getCartDetailEntities().isEmpty()) {
            model.addAttribute("error", "Giỏ hàng của bạn đang trống!");
            return "redirect:/client/cart";
        }

        Map<Long, Long> parsedVoucherSelections = new HashMap<>();

        for (Map.Entry<String, String> entry : voucherSelections.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("voucherSelections[")) {
                String productIdStr = key.substring(key.indexOf("[") + 1, key.indexOf("]"));
                try {
                    Long productId = Long.parseLong(productIdStr);
                    Long voucherId = Long.parseLong(entry.getValue());
                    parsedVoucherSelections.put(productId, voucherId);
                } catch (NumberFormatException ignored) {
                }
            }
        }

        Map<UserEntity, List<CartDetailEntity>> vendorGroups = new HashMap<>();
        for (CartDetailEntity detail : cartDetails) {
            ProductEntity product = detail.getProductEntity();
            UserEntity vendor = product.getUser();
            vendorGroups.computeIfAbsent(vendor, k -> new ArrayList<>()).add(detail);
        }


        if ("COD".equals(paymentMethod)) {
            this.method = "COD";
            orderSuccess = true;

            for (Map.Entry<UserEntity, List<CartDetailEntity>> entry : vendorGroups.entrySet()) {
                List<CartDetailEntity> vendorCartDetails = entry.getValue();

                BigDecimal totalCost = BigDecimal.ZERO;

                OrderEntity order = new OrderEntity();
                order.setUserEntity(userEntity);
                order.setCreatedAt(LocalDateTime.now());
                order.setUpdatedAt(LocalDateTime.now());
                order.setStatus(-1);
                order.setPaymentStatus(0);
                order.setMethod(paymentMethod);
                order.setEmail(email);
                order.setPhone(phone);
                order.setAddress(address);
                order.setFullName(fullName);

                List<OrderDetailEntity> orderDetails = new ArrayList<>();

                VoucherEntity singleProductVoucher = null;

                for (CartDetailEntity detail : vendorCartDetails) {
                    ProductEntity product = detail.getProductEntity();
                    int quantity = detail.getQuantity();
                    BigDecimal priceOfOne = detail.getPriceOfOne();
                    BigDecimal lineTotal = priceOfOne.multiply(BigDecimal.valueOf(quantity));

                    Long productId = product.getId();
                    Long voucherId = parsedVoucherSelections.get(productId);
                    BigDecimal discount = BigDecimal.ZERO;

                    VoucherEntity appliedVoucher = null;

                    if (voucherId != null) {
                        Optional<VoucherEntity> voucherOpt = voucherRepository.findById(voucherId);
                        if (voucherOpt.isPresent()) {
                            appliedVoucher = voucherOpt.get();

                            if (appliedVoucher.getAmount() != null) {
                                discount = discount.add(BigDecimal.valueOf(appliedVoucher.getAmount()));
                            }
                            if (appliedVoucher.getPercentDecrease() != null) {
                                BigDecimal percentDiscount = lineTotal
                                        .multiply(BigDecimal.valueOf(appliedVoucher.getPercentDecrease()))
                                        .divide(BigDecimal.valueOf(100));
                                discount = discount.add(percentDiscount);
                            }
                            if (discount.compareTo(lineTotal) >= 0) {
                                lineTotal = BigDecimal.ZERO;
                            } else {
                                lineTotal = lineTotal.subtract(discount);
                            }

                            if (vendorCartDetails.size() == 1) {
                                singleProductVoucher = appliedVoucher;
                            }
                        }
                    }

                    OrderDetailEntity detailEntity = new OrderDetailEntity();
                    detailEntity.setOrder(order);
                    detailEntity.setProduct(product);
                    detailEntity.setQuantity(quantity);
                    detailEntity.setCreatedAt(LocalDateTime.now());
                    detailEntity.setPriceOfOne(priceOfOne);
                    detailEntity.setTotalPrice(lineTotal);
                    if (appliedVoucher != null && vendorCartDetails.size() > 1) {
                        detailEntity.setVoucherEntity(appliedVoucher);
                    }

                    orderDetails.add(detailEntity);
                    totalCost = totalCost.add(lineTotal);

                    product.setAmount(product.getAmount() - quantity);
                    productRepository.save(product);

                    cartDetailRepository.delete(detail);
                }

                if (singleProductVoucher != null) {
                    order.setVoucherEntity(singleProductVoucher);
                }

                order.setTotalCost(totalCost);
                orderRepository.save(order);
                for (OrderDetailEntity orderDetail : orderDetails) {
                    orderDetailRepository.save(orderDetail);
                }
            }


            cartEntity.setCartDetailEntities(new HashSet<>());
            cartRepository.save(cartEntity);


            model.addAttribute("orderSuccess", true);
            return "redirect:/client/checkout/after-checkout";
        }

        if ("VNPay".equals(paymentMethod)) {
            this.method = "VNPay";
            this.address = address;
            this.fullName = fullName;
            this.email = email;
            this.phone = phone;

            BigDecimal combinedTotalCost = BigDecimal.ZERO;

            String vnp_TxnRef = VNPayConfig.getRandomNumber(8);

            for (Map.Entry<UserEntity, List<CartDetailEntity>> entry : vendorGroups.entrySet()) {
                List<CartDetailEntity> vendorCartDetails = entry.getValue();

                BigDecimal totalCost = BigDecimal.ZERO;

                OrderEntity order = new OrderEntity();
                order.setUserEntity(userEntity);
                order.setCreatedAt(LocalDateTime.now());
                order.setUpdatedAt(LocalDateTime.now());
                order.setStatus(-1);
                order.setPaymentStatus(0);
                order.setMethod(paymentMethod);
                order.setEmail(email);
                order.setPhone(phone);
                order.setAddress(address);
                order.setFullName(fullName);

                List<OrderDetailEntity> orderDetails = new ArrayList<>();
                VoucherEntity singleProductVoucher = null;

                for (CartDetailEntity detail : vendorCartDetails) {
                    ProductEntity product = detail.getProductEntity();
                    int quantity = detail.getQuantity();
                    BigDecimal priceOfOne = detail.getPriceOfOne();
                    BigDecimal lineTotal = priceOfOne.multiply(BigDecimal.valueOf(quantity));

                    Long productId = product.getId();
                    Long voucherId = parsedVoucherSelections.get(productId);
                    BigDecimal discount = BigDecimal.ZERO;

                    VoucherEntity appliedVoucher = null;

                    if (voucherId != null) {
                        Optional<VoucherEntity> voucherOpt = voucherRepository.findById(voucherId);
                        if (voucherOpt.isPresent()) {
                            appliedVoucher = voucherOpt.get();

                            if (appliedVoucher.getAmount() != null) {
                                discount = discount.add(BigDecimal.valueOf(appliedVoucher.getAmount()));
                            }
                            if (appliedVoucher.getPercentDecrease() != null) {
                                BigDecimal percentDiscount = lineTotal
                                        .multiply(BigDecimal.valueOf(appliedVoucher.getPercentDecrease()))
                                        .divide(BigDecimal.valueOf(100));
                                discount = discount.add(percentDiscount);
                            }
                            if (discount.compareTo(lineTotal) >= 0) {
                                lineTotal = BigDecimal.ZERO;
                            } else {
                                lineTotal = lineTotal.subtract(discount);
                            }

                            if (vendorCartDetails.size() == 1) {
                                singleProductVoucher = appliedVoucher;
                            }
                        }
                    }

                    OrderDetailEntity detailEntity = new OrderDetailEntity();
                    detailEntity.setOrder(order);
                    detailEntity.setProduct(product);
                    detailEntity.setQuantity(quantity);
                    detailEntity.setPriceOfOne(priceOfOne);
                    detailEntity.setCreatedAt(LocalDateTime.now());
                    detailEntity.setTotalPrice(lineTotal);
                    if (appliedVoucher != null && vendorCartDetails.size() > 1) {
                        detailEntity.setVoucherEntity(appliedVoucher);
                    }

                    orderDetails.add(detailEntity);
                    totalCost = totalCost.add(lineTotal);

                    product.setAmount(product.getAmount() - quantity);
                    productRepository.save(product);

                    cartDetailRepository.delete(detail);
                }

                if (singleProductVoucher != null) {
                    order.setVoucherEntity(singleProductVoucher);
                }

                order.setTotalCost(totalCost);
                orderRepository.save(order);
                for (OrderDetailEntity orderDetail : orderDetails) {
                    orderDetailRepository.save(orderDetail);
                }

                combinedTotalCost = combinedTotalCost.add(totalCost);
            }


            cartEntity.setCartDetailEntities(new HashSet<>());
            cartRepository.save(cartEntity);

            String vnp_TmnCode = VNPayConfig.vnp_TmnCode;
            String vnp_Version = "2.1.0";
            String vnp_Command = "pay";
            String vnp_IpAddr = "127.0.0.1";
            String vnp_CurrCode = "VND";
            String vnp_OrderInfo = "Thanh toán đơn hàng #" + vnp_TxnRef;
            String vnp_BankCode = "NCB";

            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", vnp_Version);
            vnp_Params.put("vnp_Command", vnp_Command);
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.put("vnp_Amount", String.valueOf(combinedTotalCost.multiply(BigDecimal.valueOf(100)).longValue()));
            vnp_Params.put("vnp_CurrCode", vnp_CurrCode);
            vnp_Params.put("vnp_BankCode", vnp_BankCode);
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
            vnp_Params.put("vnp_OrderType", "other");
            vnp_Params.put("vnp_Locale", "vn");
            vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
            vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            String vnp_CreateDate = formatter.format(calendar.getTime());
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

            calendar.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(calendar.getTime());
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
            Collections.sort(fieldNames);

            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();

            for (String fieldName : fieldNames) {
                String fieldValue = vnp_Params.get(fieldName);
                if (fieldValue != null && !fieldValue.isEmpty()) {
                    hashData.append(fieldName).append('=')
                            .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII)).append('=')
                            .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    if (fieldNames.indexOf(fieldName) < fieldNames.size() - 1) {
                        hashData.append('&');
                        query.append('&');
                    }
                }
            }

            String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
            query.append("&vnp_SecureHash=").append(vnp_SecureHash);

            String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + query;
            return "redirect:" + paymentUrl;
        }
        return "checkout";
    }

    @GetMapping("/getPaymentInfo")
    public String getPaymentInfo(@RequestParam("vnp_TransactionNo") String transactionNo,
                                 @RequestParam("vnp_OrderInfo") String orderInfo,
                                 @RequestParam("vnp_ResponseCode") String vnp_ResponseCode,
                                 @RequestParam("vnp_TransactionStatus") String transactionStatus) {

        this.transactionNo = transactionNo;
        this.orderInfo = orderInfo;
        this.transactionStatus = transactionStatus;

        String vnpTxnRef = orderInfo.replaceAll("[^0-9]", "");

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow();

        List<OrderEntity> relatedOrders = orderRepository.findByVnpTxnRef(vnpTxnRef);

        if ("00".equals(vnp_ResponseCode)) {
            for (OrderEntity order : relatedOrders) {
                order.setPaymentStatus(1);
                order.setStatus(0);
                order.setUpdatedAt(LocalDateTime.now());
                orderRepository.save(order);

                for (OrderDetailEntity detail : order.getOrderDetailEntities()) {
                    ProductEntity product = detail.getProduct();
                    product.setAmount(product.getAmount() - detail.getQuantity());
                    productRepository.save(product);
                }
            }

            CartEntity cartEntity = userEntity.getCartEntity();
            cartDetailRepository.deleteAll(cartEntity.getCartDetailEntities());
            cartEntity.setCartDetailEntities(new HashSet<>());
            cartRepository.save(cartEntity);

            orderSuccess = true;
        } else {
            for (OrderEntity order : relatedOrders) {
                order.setStatus(-2);
                order.setPaymentStatus(0);
                order.setUpdatedAt(LocalDateTime.now());
                orderRepository.save(order);
            }

            orderSuccess = false;
        }

        this.orderSuccess = orderSuccess;
        return "redirect:/client/checkout/after-checkout";
    }


    @GetMapping("/after-checkout")
    public String afterCheckout() {
        return "thankYou";
    }
}
