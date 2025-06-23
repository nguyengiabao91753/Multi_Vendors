package com.example.shopee.controller.vendor;

import com.example.shopee.entity.UserEntity;
import com.example.shopee.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.time.Year;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/vendor/dashboard")
public class VendorHomeController {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private VoucherRepository voucherRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("")
    public String home(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return "redirect:/login";
        }

        UserEntity vendor = userOptional.get();
        Long vendorId = vendor.getId();

        int productCount = productRepository.countByUserId(vendorId);
        int feedbackCount = feedbackRepository.countByVendorId(vendorId);
        int voucherCount = voucherRepository.countByUserId(vendorId);

        int currentYear = Year.now().getValue();
        Map<Integer, BigDecimal> revenueByMonth = new HashMap<>();
        for (int month = 1; month <= 12; month++) {
            BigDecimal revenue = orderDetailRepository.getMonthlyRevenueForVendor(month, vendorId, currentYear);
            revenueByMonth.put(month, revenue != null ? revenue : BigDecimal.ZERO);
        }

        model.addAttribute("productCount", productCount);
        model.addAttribute("feedbackCount", feedbackCount);
        model.addAttribute("voucherCount", voucherCount);
        model.addAttribute("revenueByMonth", revenueByMonth);

        return "vendor/dashboard";
    }
}

