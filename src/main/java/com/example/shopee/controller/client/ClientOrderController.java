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

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/client/order")
public class ClientOrderController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;


    @GetMapping("")
    public String orderHistory(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return "redirect:/login";
        }

        UserEntity user = optionalUser.get();
        Set<OrderEntity> orders = user.getOrderEntities();
        model.addAttribute("orders", orders);
        return "history";
    }


    @GetMapping("/detail/{id}")
    public String orderDetail(@PathVariable Long id, Model model) {
        Optional<OrderEntity> orderOptional = orderRepository.findById(id);
        if (orderOptional.isEmpty()) {
            return "redirect:/client/order";
        }

        OrderEntity order = orderOptional.get();
        model.addAttribute("order", order);
        return "history-detail";
    }

}
