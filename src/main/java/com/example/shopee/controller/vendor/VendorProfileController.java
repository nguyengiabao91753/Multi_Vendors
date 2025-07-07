package com.example.shopee.controller.vendor;

import com.example.shopee.entity.UserEntity;
import com.example.shopee.repository.*;
import com.example.shopee.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.Year;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/vendor/profile")
public class VendorProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @GetMapping("")
    public String profile(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        userOpt.ifPresent(user -> model.addAttribute("user", user));
        return "vendor/profile";
    }


    @PostMapping("")
    public String updateProfile(@RequestParam("fullName") String fullName,
                                @RequestParam("phone") String phone,
                                @RequestParam("address") String address,
                                @RequestParam("avatarFile") MultipartFile avatarFile,
                                RedirectAttributes redirectAttributes) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found!");
            return "redirect:/vendor/profile";
        }

        UserEntity existingUser = userOptional.get();

        existingUser.setFullName(fullName);
        existingUser.setPhone(phone);
        existingUser.setAddress(address);

        if (avatarFile != null && !avatarFile.isEmpty()) {
            String urlImg = cloudinaryService.uploadFile(avatarFile);
            existingUser.setAvatar(urlImg);
        }

        userRepository.save(existingUser);

        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        return "redirect:/vendor/profile";
    }


    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/change-password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 RedirectAttributes redirectAttributes) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("passwordError", "User not found!");
            return "redirect:/vendor/profile";
        }

        UserEntity user = userOptional.get();

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            redirectAttributes.addFlashAttribute("passwordError", "Current password is incorrect!");
            return "redirect:/vendor/profile";
        }

        if (newPassword.equals(currentPassword)) {
            redirectAttributes.addFlashAttribute("passwordError", "New password must be different from the current password!");
            return "redirect:/vendor/profile";
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("passwordError", "New password and confirmation do not match!");
            return "redirect:/vendor/profile";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("passwordSuccess", "Password changed successfully!");
        return "redirect:/vendor/profile";
    }

}

