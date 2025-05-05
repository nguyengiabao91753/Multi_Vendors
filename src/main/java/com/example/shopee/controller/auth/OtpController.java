package com.example.shopee.controller.auth;

import com.example.shopee.config.Constants;
import com.example.shopee.entity.*;
import com.example.shopee.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Controller

public class OtpController {
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final EmailSenderService emailSenderService;
    Constants constants = new Constants();

    public OtpController(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder, EmailSenderService emailSenderService) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.emailSenderService = emailSenderService;
    }

    @RequestMapping(value = "otp-check", method = RequestMethod.GET)
    public String indexOtp() {
        return "otpConfirm";
    }
    @RequestMapping(value = "confirm-otp", method = RequestMethod.POST)
    public String checkOtp(HttpSession session, @RequestParam("otp") String otp, Model model) {
        String otpRegister = (String) session.getAttribute("otp-register");
        if (otp.equals(otpRegister)) {
            UserEntity userEntity = new UserEntity();
            userEntity.setEmail((String) session.getAttribute("email"));
            userEntity.setPassword(passwordEncoder.encode((String) session.getAttribute("password")));
            String phone = (String) session.getAttribute("phone");
            String fullName = (String) session.getAttribute("fullName");
            String address = (String) session.getAttribute("address");
            String type = (String) session.getAttribute("type");
            userEntity.setPhone(phone);
            userEntity.setStatus(1);

            RoleEntity role = new RoleEntity();
            
            if(type.equals("VENDOR")) {
                role = roleService.findById(2L).get();
            } else if(type.equals("CLIENT")) {
                role = roleService.findById(3L).get();
            }

            Set<RoleEntity> roleEntities = new HashSet<>();

            roleEntities.add(role);
            UserEntity userNew = new UserEntity();
            userNew.setFullName(fullName);
            userNew.setStatus(1);
            userNew.setAddress(address);

            CartEntity cartEntity = new CartEntity();
            cartEntity.setTotalCost(BigDecimal.valueOf(0));
            cartEntity.setCreatedBy("ADMIN");
            cartEntity.setUpdatedBy("ADMIN");
            cartEntity.setCreatedAt(LocalDateTime.now());
            cartEntity.setUpdatedAt(LocalDateTime.now());
            Set<CartDetailEntity> cartDetailEntities = new HashSet<>();
            cartEntity.setCartDetailEntities(cartDetailEntities);
            userEntity.setRoleEntities(roleEntities);
            userEntity.setCreatedBy("ADMIN");
            userEntity.setUpdatedBy("ADMIN");
            userNew.setCreatedBy("ADMIN");
            userNew.setUpdatedBy("ADMIN");
            userNew.setCartEntity(cartEntity);
            userService.saveUser(userEntity);
            return "redirect:/";
        }
        model.addAttribute("mess","OTP is not correct! Please check your email.");
        return "otpConfirm";
    }

}