package com.example.shopee.controller.auth;

import com.example.shopee.config.Constants;
import com.example.shopee.entity.*;
import com.example.shopee.repository.UserRepository;
import com.example.shopee.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @Autowired
    private UserRepository userRepository;

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
            userEntity.setPhone(phone);
            userEntity.setStatus(1);

            RoleEntity role1 = new RoleEntity();
            RoleEntity role2 = new RoleEntity();


            role1 = roleService.findById(2L).get();
            role2 = roleService.findById(3L).get();

            Set<RoleEntity> roleEntities = new HashSet<>();

            roleEntities.add(role1);
            roleEntities.add(role2);

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
            return "redirect:/login";
        }
        model.addAttribute("mess", "OTP is not correct! Please check your email.");
        return "otpConfirm";
    }

    @RequestMapping(value = "forgot", method = RequestMethod.GET)
    public String forgot() {
        return "forgot";
    }


    @RequestMapping(value = "forgotPass", method = RequestMethod.POST)
    public String forgotPass(@RequestParam String email, Model model, HttpSession session) {
        if (!userService.findByEmail(email).isPresent()) {
            model.addAttribute("mess", "Email does not exist!");
            return "forgot";
        }

        session.setAttribute("otp-pass", otpCode());
        session.setMaxInactiveInterval(360); // Session timeout set to 6 minutes

        String subject = "Your OTP Code";
        String mess = "Hello,\n" + email + "\nHere is your OTP code: " + session.getAttribute("otp-pass") + "\nPlease enter it in the form.\nThank you!";

        this.emailSenderService.sendEmail(email, subject, mess);
        session.setAttribute("email", email);

        return "redirect:/otp-check-pass";
    }


    @RequestMapping(value = "otp-check-pass", method = RequestMethod.GET)
    public String indexOtpPass() {
        return "otpConfirmPass";
    }

    @RequestMapping(value = "confirm-otp-pass", method = RequestMethod.POST)
    public String checkOtpPass(HttpSession session,
                               @RequestParam("otp") String otp,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        String otpRegister = (String) session.getAttribute("otp-pass");

        if (otp.equals(otpRegister)) {
            redirectAttributes.addFlashAttribute("mess", "OTP is correct. Please reset your password.");
            return "redirect:/change-pass";
        }

        model.addAttribute("mess", "Incorrect OTP! Please try again.");
        return "otpConfirmPass";
    }


    @RequestMapping(value = "change-pass", method = RequestMethod.GET)
    public String indexResetPass() {
        return "changePass";
    }

    @RequestMapping(value = "change-pass", method = RequestMethod.POST)
    public String reset(HttpSession session,
                        @RequestParam("pass") String pass,
                        RedirectAttributes redirectAttributes) {

        String email = (String) session.getAttribute("email");
        UserEntity userEntity = userRepository.findByEmail(email).get();

        userEntity.setPassword(passwordEncoder.encode(pass));
        userService.saveUser(userEntity);

        redirectAttributes.addFlashAttribute("mess", "Password has been successfully reset!");
        return "redirect:/change-pass";
    }

    public String otpCode() {
        int code = (int) Math.floor(((Math.random() * 899999) + 100000));
        return String.valueOf(code);
    }
}