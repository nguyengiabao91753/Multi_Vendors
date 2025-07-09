package com.example.shopee.controller.client;

import com.example.shopee.entity.FeedbackEntity;
import com.example.shopee.entity.ProductEntity;
import com.example.shopee.entity.UserEntity;
import com.example.shopee.repository.FeedbackRepository;
import com.example.shopee.repository.OrderRepository;
import com.example.shopee.repository.ProductRepository;
import com.example.shopee.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/client")
public class ClientHomeController {

    @Autowired
    private ProductRepository productRepository;


    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/home")
    public String home(Model model) {
        return "index";
    }




}
