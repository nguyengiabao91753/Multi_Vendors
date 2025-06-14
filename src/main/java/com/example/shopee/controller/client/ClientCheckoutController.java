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

    private Long voucherId;
    private String voucherName;
    private BigDecimal totalCost;

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

    public List<VoucherEntity> getApplicableVouchers(Set<CartDetailEntity> cartDetails) {
        LocalDateTime now = LocalDateTime.now();
        Set<VoucherEntity> applicableVouchers = new HashSet<>();

        List<VoucherEntity> adminVouchers = voucherRepository.findAll().stream()
                .filter(v -> v.getUserEntity() != null)
                .filter(v -> v.getUserEntity().getRoleEntities().stream().anyMatch(role -> role.getId() == 1L))
                .filter(v -> v.getAmount() != null && v.getAmount() > 0)
                .filter(v -> v.getStatus() == 1)
                .filter(v -> (v.getStartTime() == null || !v.getStartTime().isAfter(now)) &&
                        (v.getEndTime() == null || !v.getEndTime().isBefore(now)))
                .collect(Collectors.toList());

        applicableVouchers.addAll(adminVouchers);

        Set<Long> vendorIds = cartDetails.stream()
                .map(cd -> cd.getProductEntity().getUser().getId())
                .collect(Collectors.toSet());

        for (Long vendorId : vendorIds) {
            List<VoucherEntity> vendorVouchers = voucherRepository.findAllBySellerId(vendorId).stream()
                    .filter(v -> v.getAmount() != null && v.getAmount() > 0)
                    .filter(v -> v.getStatus() == 1)
                    .filter(v -> (v.getStartTime() == null || !v.getStartTime().isAfter(now)) &&
                            (v.getEndTime() == null || !v.getEndTime().isBefore(now)))
                    .collect(Collectors.toList());
            applicableVouchers.addAll(vendorVouchers);
        }

        return new ArrayList<>(applicableVouchers);
    }


    @GetMapping("")
    public String checkout(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return "redirect:/login";
        }

        UserEntity user = optionalUser.get();
        Optional<CartEntity> cartOpt = cartRepository.findByUserEntity(user);

        if (cartOpt.isEmpty()) {
            model.addAttribute("cartDetails", Collections.emptyList());
            model.addAttribute("totalPrice", BigDecimal.ZERO);
            return "checkout";
        }

        CartEntity cart = cartOpt.get();
        Set<CartDetailEntity> cartDetails = cart.getCartDetailEntities();

        BigDecimal totalPrice = cartDetails.stream()
                .map(cd -> cd.getPriceOfOne().multiply(BigDecimal.valueOf(cd.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<VoucherEntity> vouchers = getApplicableVouchers(cartDetails);

        model.addAttribute("cartDetails", cartDetails);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("vouchers", vouchers);

        return "checkout";
    }

    @PostMapping("")
    public String checkoutPage(Model model, @RequestParam("paymentMethod") String paymentMethod, @RequestParam(name = "voucherId", required = false) Long voucherId,
                               @RequestParam("fullName") String fullName,
                               @RequestParam("phone") String phone,
                               @RequestParam("email") String email,
                               @RequestParam("address") String address,
                               HttpServletRequest request) throws UnsupportedEncodingException {

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(userEmail).orElseThrow();

        CartEntity cartEntity = userEntity.getCartEntity();
        if (cartEntity == null || cartEntity.getCartDetailEntities().isEmpty()) {
            model.addAttribute("error", "Giỏ hàng của bạn đang trống!");
            return "redirect:/client/cart";
        }


        OrderEntity order = new OrderEntity();
        BigDecimal totalCost = cartEntity.getTotalCost();

        if (totalCost.compareTo(BigDecimal.ZERO) <= 0) {
            model.addAttribute("error", "Tổng giá lỗi!");
            return "redirect:/client/cart";
        }


        BigDecimal discount = BigDecimal.ZERO;

        if (voucherId != null) {
            Optional<VoucherEntity> voucherOpt = voucherRepository.findById(voucherId);
            if (voucherOpt.isPresent()) {
                VoucherEntity voucher = voucherOpt.get();
                this.voucherId = voucherId;
                this.voucherName = voucher.getName();

                if (voucher.getAmount() != null) {
                    discount = discount.add(BigDecimal.valueOf(voucher.getAmount()));
                }
                if (voucher.getPercentDecrease() != null) {
                    BigDecimal percentDiscount = totalCost
                            .multiply(BigDecimal.valueOf(voucher.getPercentDecrease()))
                            .divide(BigDecimal.valueOf(100));
                    discount = discount.add(percentDiscount);
                }

                if (discount.compareTo(totalCost) >= 0) {
                    totalCost = BigDecimal.ZERO;
                } else {
                    totalCost = totalCost.subtract(discount);
                }

                order.setVoucherEntity(voucher);
                voucher.setAmount(voucher.getAmount() - 1);
                voucherRepository.save(voucher);
            }
        }

        order.setTotalCost(totalCost);

        order.setUserEntity(userEntity);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setStatus(-1);
        order.setEmail(email);
        order.setPhone(phone);
        order.setAddress(address);
        order.setFullName(fullName);

        if ("COD".equals(paymentMethod)) {
            order.setMethod("COD");
            orderSuccess = true;
            Set<OrderDetailEntity> orderDetailEntities = new HashSet<>();

            for (CartDetailEntity cartDetailEntity : cartEntity.getCartDetailEntities()) {
                OrderDetailEntity orderDetailEntity = new OrderDetailEntity();
                orderDetailEntity.setProductEntity(cartDetailEntity.getProductEntity());
                orderDetailEntity.setPriceOfOne(cartDetailEntity.getPriceOfOne());
                orderDetailEntity.setQuantity(cartDetailEntity.getQuantity());
                orderDetailEntity.setTotalPrice(
                        cartDetailEntity.getPriceOfOne().multiply(BigDecimal.valueOf(cartDetailEntity.getQuantity()))
                );
                orderDetailEntity.setOrderEntity(order);
                orderDetailEntities.add(orderDetailEntity);
                ProductEntity product = cartDetailEntity.getProductEntity();
                product.setAmount(product.getAmount() - cartDetailEntity.getQuantity());
                productRepository.save(product);
            }
            this.method = "COD";

            order.setOrderDetailEntities(orderDetailEntities);
            OrderEntity save = orderRepository.save(order);
            this.orderEntity = save;
            this.totalCost = totalCost;
            UserEntity user = cartEntity.getUserEntity();
            if (user != null) {
                user.setCartEntity(null);
            }
            cartRepository.delete(cartEntity);

            model.addAttribute("orderSuccess", true);
            return "redirect:/client/checkout/after-checkout";
        }
        if ("VNPay".equals(paymentMethod)) {
            this.method = "VNPay";
            this.address = address;
            this.fullName = fullName;
            this.email = email;
            this.phone = phone;
            Optional<VoucherEntity> voucherOpt = voucherRepository.findById(voucherId);
            if (voucherOpt.isPresent()) {
                this.voucherId = voucherId;
            }

            String vnp_TxnRef = VNPayConfig.getRandomNumber(8); // Unique order reference
            String vnp_TmnCode = VNPayConfig.vnp_TmnCode;
            String vnp_Version = "2.1.0";
            String vnp_Command = "pay";
            String vnp_IpAddr = "127.0.0.1";
            String vnp_CurrCode = "VND";
            String vnp_OrderInfo = "Thanh toán đơn hàng " + vnp_TxnRef;
            String vnp_BankCode = "NCB";
            order.setMethod("VNPay");
            HttpSession session = request.getSession();
            session.setAttribute("order", order);

            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", vnp_Version);
            vnp_Params.put("vnp_Command", vnp_Command);
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.put("vnp_Amount", String.valueOf(totalCost.multiply(BigDecimal.valueOf(100)).longValue()));
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

            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
            Collections.sort(fieldNames);

            for (String fieldName : fieldNames) {
                String fieldValue = vnp_Params.get(fieldName);
                if (fieldValue != null && !fieldValue.isEmpty()) {
                    hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString())).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    if (fieldNames.indexOf(fieldName) < fieldNames.size() - 1) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }

            String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
            query.append("&vnp_SecureHash=" + vnp_SecureHash);

            String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + query.toString();
            return "redirect:" + paymentUrl;
        }

        return "checkout";
    }

    // Handle VNPay payment callback
    @GetMapping("/getPaymentInfo")
    public String getPaymentInfo(@RequestParam("vnp_TransactionNo") String transactionNo,
                                 @RequestParam("vnp_OrderInfo") String orderInfo,
                                 @RequestParam("vnp_ResponseCode") String vnp_ResponseCode,
                                 @RequestParam("vnp_TransactionStatus") String transactionStatus,
                                 Model model) {

        this.transactionNo = transactionNo;
        this.orderInfo = orderInfo;
        this.transactionStatus = transactionStatus;

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow();
        CartEntity cartEntity = userEntity.getCartEntity();

        OrderEntity order = new OrderEntity();
        order.setUserEntity(userEntity);
        order.setAddress(this.address);
        order.setEmail(this.email);
        order.setFullName(this.fullName);
        order.setPhone(this.phone);
        order.setMethod("VNPay");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        if (Objects.equals(vnp_ResponseCode, "00")) {
            orderSuccess = true;
            order.setStatus(1);
            BigDecimal discount = BigDecimal.ZERO;
            BigDecimal totalCost = cartEntity.getTotalCost();

            if (this.voucherId != null) {
                Optional<VoucherEntity> voucherOpt = voucherRepository.findById(voucherId);
                if (voucherOpt.isPresent()) {
                    VoucherEntity voucher = voucherOpt.get();
                    this.voucherId = voucherId;
                    this.voucherName = voucher.getName();

                    if (voucher.getAmount() != null) {
                        discount = discount.add(BigDecimal.valueOf(voucher.getAmount()));
                    }
                    if (voucher.getPercentDecrease() != null) {
                        BigDecimal percentDiscount = totalCost
                                .multiply(BigDecimal.valueOf(voucher.getPercentDecrease()))
                                .divide(BigDecimal.valueOf(100));
                        discount = discount.add(percentDiscount);
                    }

                    if (discount.compareTo(totalCost) >= 0) {
                        totalCost = BigDecimal.ZERO;
                    } else {
                        totalCost = totalCost.subtract(discount);
                    }

                    order.setVoucherEntity(voucher);
                    voucher.setAmount(voucher.getAmount() - 1);
                    voucherRepository.save(voucher);
                }
            }
            order.setTotalCost(totalCost);
            this.totalCost = totalCost;


            Set<OrderDetailEntity> orderDetailEntities = new HashSet<>();

            for (CartDetailEntity cartDetailEntity : cartEntity.getCartDetailEntities()) {
                OrderDetailEntity orderDetailEntity = new OrderDetailEntity();
                orderDetailEntity.setProductEntity(cartDetailEntity.getProductEntity());
                orderDetailEntity.setPriceOfOne(cartDetailEntity.getPriceOfOne());
                orderDetailEntity.setQuantity(cartDetailEntity.getQuantity());
                orderDetailEntity.setTotalPrice(
                        cartDetailEntity.getPriceOfOne().multiply(BigDecimal.valueOf(cartDetailEntity.getQuantity()))
                );
                orderDetailEntity.setOrderEntity(order);

                orderDetailEntities.add(orderDetailEntity);

                ProductEntity product = cartDetailEntity.getProductEntity();
                product.setAmount(product.getAmount() - cartDetailEntity.getQuantity());
                productRepository.save(product);
            }
            order.setOrderDetailEntities(orderDetailEntities);
            orderRepository.save(order);

            UserEntity user = cartEntity.getUserEntity();
            if (user != null) {
                user.setCartEntity(null);
            }
            cartRepository.delete(cartEntity);
        } else {
            orderSuccess = false;
            order.setStatus(0);
            orderRepository.save(order);
        }
        this.orderEntity = order;

        this.orderSuccess = orderSuccess;
        this.transactionNo = transactionNo;
        this.orderInfo = orderInfo;
        this.transactionStatus = transactionStatus;


        return "redirect:/client/checkout/after-checkout";
    }

    @Autowired
    private EmailSenderService emailSenderService;

    @GetMapping("/after-checkout")
    public String afterCheckout(Model model) {
        model.addAttribute("orderSuccess", this.orderSuccess);
        model.addAttribute("transactionNo", this.transactionNo);
        model.addAttribute("orderInfo", this.orderInfo);
        model.addAttribute("transactionStatus", this.transactionStatus);
        model.addAttribute("order", this.orderEntity);
        model.addAttribute("totalCost", this.totalCost);
        model.addAttribute("voucherName", this.voucherName);
        model.addAttribute("method", this.method);
        String subject = "Thanh toán thành công";
        String mess = "Xin chào @" + " \n" + email + "Bạn đã thanh toán thành công đơn hàng có mã đơn hàng là: " + transactionNo + ", tổng giá trị là " + totalCost + " !" + "\n Cảm ơn đã tin tưởng chúng tôi để mua hàng!";
        this.emailSenderService.sendEmail(email, subject, mess);

        return "after-checkout";
    }
}
