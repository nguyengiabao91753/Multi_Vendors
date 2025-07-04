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
    private EmailSenderService emailSenderService;

    private Long voucherId;
    private Long itemId;
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


    @GetMapping("/{itemId}")
    public String checkout(@PathVariable("itemId") Long itemId, Model model) {
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

        Optional<CartDetailEntity> optionalCartDetail = cart.getCartDetailEntities().stream()
                .filter(cd -> cd.getId().equals(itemId))
                .findFirst();

        if (optionalCartDetail.isEmpty()) {
            return "redirect:/cart";
        }
        this.itemId = itemId;

        CartDetailEntity cartDetail = optionalCartDetail.get();

        Set<CartDetailEntity> cartDetails = Set.of(cartDetail);

        BigDecimal totalPrice = cartDetail.getPriceOfOne()
                .multiply(BigDecimal.valueOf(cartDetail.getQuantity()));

        List<VoucherEntity> vouchers = getApplicableVouchers(cartDetails);

        model.addAttribute("cartDetails", cartDetails);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("vouchers", vouchers);

        return "checkout";
    }

    @Autowired
    private CartDetailRepository cartDetailRepository;

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

        BigDecimal totalCost = cartEntity.getTotalCost();

        if (totalCost.compareTo(BigDecimal.ZERO) <= 0) {
            model.addAttribute("error", "Tổng giá lỗi!");
            return "redirect:/client/cart";
        }

        if ("COD".equals(paymentMethod)) {
            this.method = "COD";
            orderSuccess = true;

            Optional<CartDetailEntity> cartDetailOpt = cartDetailRepository.findById(this.itemId);
            CartDetailEntity cartDetail = cartDetailOpt.get();

            ProductEntity product = cartDetail.getProductEntity();

            BigDecimal itemTotal = cartDetail.getPriceOfOne()
                    .multiply(BigDecimal.valueOf(cartDetail.getQuantity()));

            OrderEntity singleOrder = new OrderEntity();
            singleOrder.setUserEntity(userEntity);
            singleOrder.setCreatedAt(LocalDateTime.now());
            singleOrder.setUpdatedAt(LocalDateTime.now());
            singleOrder.setStatus(-1);
            singleOrder.setPaymentStatus(0);
            singleOrder.setMethod("COD");
            singleOrder.setEmail(email);
            singleOrder.setPhone(phone);
            singleOrder.setAddress(address);
            singleOrder.setFullName(fullName);
            singleOrder.setTotalCost(itemTotal);
            singleOrder.setProductEntity(product);
            singleOrder.setPriceOfOne(product.getSalePrice());
            singleOrder.setQuantity(cartDetail.getQuantity());

            if (voucherId != null) {
                Optional<VoucherEntity> voucherOpt = voucherRepository.findById(voucherId);
                if (voucherOpt.isPresent()) {
                    VoucherEntity voucher = voucherOpt.get();
                    singleOrder.setVoucherEntity(voucher);
                }
            }

            orderRepository.save(singleOrder);

            product.setAmount(product.getAmount() - cartDetail.getQuantity());
            productRepository.save(product);

            cartEntity.getCartDetailEntities().remove(cartDetail);
            cartDetailRepository.delete(cartDetail);
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
            BigDecimal discount = BigDecimal.ZERO;
            Optional<VoucherEntity> voucherOpt = voucherRepository.findById(voucherId);
            if (voucherOpt.isPresent()) {
                VoucherEntity voucher = voucherOpt.get();
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
            }

            String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
            String vnp_TmnCode = VNPayConfig.vnp_TmnCode;
            String vnp_Version = "2.1.0";
            String vnp_Command = "pay";
            String vnp_IpAddr = "127.0.0.1";
            String vnp_CurrCode = "VND";
            String vnp_OrderInfo = "Thanh toan don hang " + vnp_TxnRef;
            String vnp_BankCode = "NCB";

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

    @GetMapping("/getPaymentInfo")
    public String getPaymentInfo(@RequestParam("vnp_TransactionNo") String transactionNo,
                                 @RequestParam("vnp_OrderInfo") String orderInfo,
                                 @RequestParam("vnp_ResponseCode") String vnp_ResponseCode,
                                 @RequestParam("vnp_TransactionStatus") String transactionStatus) {

        this.transactionNo = transactionNo;
        this.orderInfo = orderInfo;
        this.transactionStatus = transactionStatus;

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow();
        CartEntity cartEntity = userEntity.getCartEntity();

        boolean orderSuccess = false;

        if (Objects.equals(vnp_ResponseCode, "00")) {
            orderSuccess = true;

            BigDecimal totalCost = cartEntity.getTotalCost();
            BigDecimal discount = BigDecimal.ZERO;

            VoucherEntity appliedVoucher = null;

            if (this.voucherId != null) {
                Optional<VoucherEntity> voucherOpt = voucherRepository.findById(voucherId);
                if (voucherOpt.isPresent()) {
                    appliedVoucher = voucherOpt.get();

                    if (appliedVoucher.getAmount() != null) {
                        discount = discount.add(BigDecimal.valueOf(appliedVoucher.getAmount()));
                    }
                    if (appliedVoucher.getPercentDecrease() != null) {
                        BigDecimal percentDiscount = totalCost
                                .multiply(BigDecimal.valueOf(appliedVoucher.getPercentDecrease()))
                                .divide(BigDecimal.valueOf(100));
                        discount = discount.add(percentDiscount);
                    }

                    if (discount.compareTo(totalCost) >= 0) {
                        totalCost = BigDecimal.ZERO;
                    } else {
                        totalCost = totalCost.subtract(discount);
                    }

                    appliedVoucher.setAmount(appliedVoucher.getAmount() - 1);
                    voucherRepository.save(appliedVoucher);
                }
            }
            Optional<CartDetailEntity> cartDetailOpt = cartDetailRepository.findById(this.itemId);
            CartDetailEntity cartDetail = cartDetailOpt.get();

            ProductEntity product = cartDetail.getProductEntity();

            BigDecimal itemTotal = cartDetail.getPriceOfOne()
                    .multiply(BigDecimal.valueOf(cartDetail.getQuantity()));

            OrderEntity order = new OrderEntity();
            order.setUserEntity(userEntity);
            order.setAddress(this.address);
            order.setEmail(this.email);
            order.setFullName(this.fullName);
            order.setPhone(this.phone);
            order.setMethod("VNPay");
            order.setCreatedAt(LocalDateTime.now());
            order.setUpdatedAt(LocalDateTime.now());
            order.setStatus(-1);
            order.setPaymentStatus(1);
            order.setPriceOfOne(product.getSalePrice());
            order.setTotalCost(itemTotal);
            order.setProductEntity(product);
            order.setQuantity(cartDetail.getQuantity());

            if (appliedVoucher != null) {
                order.setVoucherEntity(appliedVoucher);
            }

            orderRepository.save(order);

            product.setAmount(product.getAmount() - cartDetail.getQuantity());
            productRepository.save(product);

            cartEntity.getCartDetailEntities().remove(cartDetail);
            cartDetailRepository.delete(cartDetail);
            cartRepository.save(cartEntity);

        } else {
            orderSuccess = false;

            OrderEntity failedOrder = new OrderEntity();
            failedOrder.setUserEntity(userEntity);
            failedOrder.setAddress(this.address);
            failedOrder.setEmail(this.email);
            failedOrder.setFullName(this.fullName);
            failedOrder.setPhone(this.phone);
            failedOrder.setMethod("VNPay");
            failedOrder.setCreatedAt(LocalDateTime.now());
            failedOrder.setUpdatedAt(LocalDateTime.now());
            failedOrder.setStatus(0);
            failedOrder.setPaymentStatus(0);
            orderRepository.save(failedOrder);
        }

        this.orderSuccess = orderSuccess;
        return "redirect:/client/checkout/after-checkout";
    }


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
        if (email != null && !email.trim().isEmpty()) {
            String subject = "Thanh toán thành công";
            String mess = "Xin chào " + email + "\n"
                    + "Bạn đã thanh toán thành công đơn hàng có mã đơn hàng là: " + transactionNo
                    + ", tổng giá trị là " + totalCost + "!\n"
                    + "Cảm ơn bạn đã tin tưởng và mua hàng của chúng tôi!";

            this.emailSenderService.sendEmail(email, subject, mess);
        } else {
            System.err.println("⚠ Email bị null hoặc rỗng. Không thể gửi thông báo.");
        }

        return "thankYou";
    }
}
