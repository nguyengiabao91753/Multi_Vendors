package com.example.shopee.controller.admin;


import com.example.shopee.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Year;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminHomeController {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dashboard")
    public String home(Model model) {
        long productCount = productRepository.count();
        long feedbackCount = feedbackRepository.count();
        long orderCount = orderRepository.count();
        long userCount = userRepository.count();

        int currentYear = Year.now().getValue();
        Map<Integer, BigDecimal> revenueByMonth = new HashMap<>();
        for (int month = 1; month <= 12; month++) {
            BigDecimal revenue = orderDetailRepository.getMonthlyRevenue(month, currentYear);
            revenueByMonth.put(month, revenue != null ? revenue : BigDecimal.ZERO);
        }

        model.addAttribute("productCount", productCount);
        model.addAttribute("feedbackCount", feedbackCount);
        model.addAttribute("orderCount", orderCount);
        model.addAttribute("userCount", userCount);
        model.addAttribute("revenueByMonth", revenueByMonth);

        return "admin/dashboard";
    }
}