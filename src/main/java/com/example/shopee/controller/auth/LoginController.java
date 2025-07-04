package com.example.shopee.controller.auth;

import com.example.shopee.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("")
public class LoginController {
    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginPage(@ModelAttribute("mess") String mess) {
        System.out.println("MESS: " + mess);
        return "login";
    }


    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model) {
        boolean isValidCredentials = userService.validateCredentials(username, password);
        if (isValidCredentials) {
            return "redirect:/index";
        } else {
            model.addAttribute("mess", "Sai email hoặc mật khẩu!");
            return "login";
        }
    }
    @GetMapping("/loginError")
    public String loginError(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("mess", "Sai email hoặc mật khẩu!");
        return "redirect:/login";
    }

}

