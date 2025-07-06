package com.example.shopee.controller.admin;


import com.example.shopee.repository.*;
import com.example.shopee.service.UserService;
import jakarta.servlet.http.HttpSession;
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
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginPage(@ModelAttribute("mess") String mess) {
        System.out.println("MESS: " + mess);
        return "admin/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }


    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, Model model, HttpSession session) {
        boolean isValidCredentials = userService.validateCredentialsAdmin(email, password);
        if (isValidCredentials) {
            session.setAttribute("email", email);
            session.setAttribute("role", "ADMIN");
            return "redirect:/admin/dashboard";
        } else {
            model.addAttribute("mess", "Sai email hoặc mật khẩu!");
            return "redirect:/admin/login?error=true";
        }
    }


    @GetMapping("/dashboard")
    public String home(Model model, HttpSession session) {
        String email = (String) session.getAttribute("email");

        if (email == null) {
            return "redirect:/admin/login";
        }

        long productCount = productRepository.count();
        long feedbackCount = feedbackRepository.count();
        long orderCount = orderRepository.count();
        long userCount = userRepository.count();

        int currentYear = Year.now().getValue();
        Map<Integer, BigDecimal> revenueByMonth = new HashMap<>();
        for (int month = 1; month <= 12; month++) {
            BigDecimal revenue = orderRepository.getMonthlyRevenue(month, currentYear);
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