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
    public String profile(@RequestParam(value = "passwordError", required = false) String passwordError,
                          @RequestParam(value = "passwordSuccess", required = false) String passwordSuccess,
                          Model model) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        userOpt.ifPresent(user -> model.addAttribute("user", user));

        if (passwordError != null) {
            model.addAttribute("passwordError", passwordError);
        }
        if (passwordSuccess != null) {
            model.addAttribute("passwordSuccess", passwordSuccess);
        }

        return "profile";
    }



    @PostMapping("/profile")
    public String updateProfile(@RequestParam("fullName") String fullName,
                                @RequestParam("phone") String phone,
                                @RequestParam("address") String address,
                                @RequestParam("avatarFile") MultipartFile avatarFile,
                                RedirectAttributes redirectAttributes) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found!");
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

        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        return "redirect:/user/profile";
    }


    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/change-password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            return "redirect:/user/profile?passwordError=User+not+found";
        }

        UserEntity user = userOptional.get();

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return "redirect:/user/profile?passwordError=Current+password+is+incorrect";
        }

        if (newPassword.equals(currentPassword)) {
            return "redirect:/user/profile?passwordError=New+password+must+be+different+from+current";
        }

        if (!newPassword.equals(confirmPassword)) {
            return "redirect:/user/profile?passwordError=New+password+and+confirmation+do+not+match";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return "redirect:/user/profile?passwordSuccess=Password+changed+successfully";
    }




}