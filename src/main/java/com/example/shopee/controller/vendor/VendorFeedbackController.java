package com.example.shopee.controller.vendor;

import com.example.shopee.entity.FeedbackEntity;
import com.example.shopee.entity.UserEntity;
import com.example.shopee.entity.VoucherEntity;
import com.example.shopee.repository.FeedbackRepository;
import com.example.shopee.repository.UserRepository;
import com.example.shopee.repository.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/vendor/feedback")
public class VendorFeedbackController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("")
    public String list(Model model,
                               @RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "size", defaultValue = "5") int size) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        List<FeedbackEntity> feedbacks = feedbackRepository.findAllByProductOwner(user);

        if (keyword != null && !keyword.isEmpty()) {
            feedbacks = feedbacks.stream()
                    .filter(v -> v.getUserEntity().getEmail() != null && v.getUserEntity().getEmail().toLowerCase().contains(keyword.toLowerCase()))
                    .toList();
        }

        int totalItems = feedbacks.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);
        int start = page * size;
        int end = Math.min(start + size, totalItems);
        List<FeedbackEntity> pagedFeedbacks = feedbacks.subList(start, end);

        model.addAttribute("feedbacks", pagedFeedbacks);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "vendor/feedback/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        feedbackRepository.deleteById(id);
        return "redirect:/vendor/feedback";
    }
}
