package com.example.shopee.controller.client;

import com.example.shopee.entity.FeedbackEntity;
import com.example.shopee.entity.ProductEntity;
import com.example.shopee.entity.UserEntity;
import com.example.shopee.repository.FeedbackRepository;
import com.example.shopee.repository.OrderDetailRepository;
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
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/client")
public class ClientHomeController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/home")
    public String home(Model model) {
        return "index";
    }

    @PostMapping("/feedback/submit")
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

        boolean bought = orderDetailRepository.hasUserBoughtProduct(productId, user.getId());
        boolean alreadyFeedback = feedbackRepository.existsByUserEntityAndProductEntity(user, product);

        if (bought && !alreadyFeedback) {
            FeedbackEntity feedback = new FeedbackEntity();
            feedback.setUserEntity(user);
            feedback.setProductEntity(product);
            feedback.setRatedStar(ratedStar);
            feedback.setComment(comment);
            feedback.setCreatedAt(LocalDateTime.now());
            feedbackRepository.save(feedback);
        }

        return "redirect:/detail/" + productId;
    }


}
