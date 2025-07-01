package com.example.shopee.controller;

import com.example.shopee.entity.UserEntity;
import com.example.shopee.repository.UserRepository;
import com.example.shopee.service.CloudinaryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
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
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        userOpt.ifPresent(user -> model.addAttribute("user", user));
        return "profile";
    }


    @PostMapping("/profile")
    public String updateProfile(@RequestParam("fullName") String fullName,
                                @RequestParam("phone") String phone,
                                @RequestParam("address") String address,
                                @RequestParam("avatarFile") MultipartFile avatarFile, RedirectAttributes redirectAttributes) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy người dùng!");
            return "redirect:/user/profile";
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

        redirectAttributes.addFlashAttribute("successMessage", "Thông tin cá nhân đã được cập nhật thành công!");
        return "redirect:/user/profile";
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
            redirectAttributes.addFlashAttribute("passwordError", "Không tìm thấy người dùng!");
            return "redirect:/user/profile";

        }

        UserEntity user = userOptional.get();

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            redirectAttributes.addFlashAttribute("passwordError", "Mật khẩu hiện tại không đúng!");
            return "redirect:/user/profile";

        }

        if (newPassword.equals(currentPassword)) {
            redirectAttributes.addFlashAttribute("passwordError", "Mật khẩu mới không được trùng với mật khẩu hiện tại!");
            return "redirect:/user/profile";

        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("passwordError", "Mật khẩu mới và xác nhận không khớp!");
            return "redirect:/user/profile";

        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("passwordSuccess", "Đổi mật khẩu thành công!");
        return "redirect:/user/profile";
    }


}