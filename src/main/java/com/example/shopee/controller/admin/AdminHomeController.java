package com.example.shopee.controller.admin;


import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin")
public class AdminHomeController {

    @GetMapping("/dashboard")
    public String home(Model model) {
        return "admin/dashboard";
    }


}