package com.example.shopee.controller.client;

import com.example.shopee.entity.*;
import com.example.shopee.enums.ReturnStatus;
import com.example.shopee.repository.*;
import com.example.shopee.service.CloudinaryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/client/review")
public class ClientReviewController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/{productId}")
    public String view(@PathVariable Long productId, Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return "redirect:/login";
        }

        UserEntity user = optionalUser.get();
        Optional<ProductEntity> productOptional = productRepository.findById(productId);

        ProductEntity product = productOptional.get();

        boolean hasFeedback = feedbackRepository.existsByUserEntityAndProductEntity(user, product);

        if (hasFeedback) {
            return "redirect:/detail/" + productId;
        }

        model.addAttribute("product", product);
        model.addAttribute("canFeedback", true);

        return "review";
    }



    @PostMapping("/submit")
    public String submitFeedback(@RequestParam Long productId,
                                 @RequestParam Integer ratedStar,
                                 @RequestParam String comment) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return "redirect:/login";
        }

        UserEntity user = optionalUser.get();

        ProductEntity product = productRepository.findById(productId).orElseThrow();

        FeedbackEntity feedback = new FeedbackEntity();
        feedback.setUserEntity(user);
        feedback.setProductEntity(product);
        feedback.setRatedStar(ratedStar);
        feedback.setComment(comment);
        feedback.setCreatedAt(LocalDateTime.now());
        feedbackRepository.save(feedback);

        return "redirect:/detail/" + productId;
    }
}
