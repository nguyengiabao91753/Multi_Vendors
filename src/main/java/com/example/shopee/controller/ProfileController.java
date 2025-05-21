package com.example.shopee.controller;

import com.example.shopee.entity.UserEntity;
import com.example.shopee.repository.UserRepository;
import com.example.shopee.service.CloudinaryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class ProfileController {

    private final UserRepository userRepository;

    @Autowired
    private CloudinaryService cloudinaryService;


    public ProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email).orElse(new UserEntity());
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("user") UserEntity updatedUser,
                                BindingResult bindingResult,
                                @RequestParam("avatarFile") MultipartFile avatarFile,
                                Model model) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            model.addAttribute("errorMessage", "Không tìm thấy người dùng.");
            return "profile";
        }

        UserEntity existingUser = userOptional.get();

        existingUser.setFullName(updatedUser.getFullName());
        existingUser.setPhone(updatedUser.getPhone());
        existingUser.setAddress(updatedUser.getAddress());
        existingUser.setDob(updatedUser.getDob());

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", existingUser);
            return "profile";
        }

        if (avatarFile != null && !avatarFile.isEmpty()) {
            String urlImg = cloudinaryService.uploadFile(avatarFile);
            existingUser.setAvatar(urlImg);
        }

        userRepository.save(existingUser);
        model.addAttribute("successMessage", "Thông tin cá nhân đã được cập nhật thành công.");
        model.addAttribute("user", existingUser);
        return "redirect:/";
    }
}