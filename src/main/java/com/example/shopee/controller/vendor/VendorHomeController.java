package com.example.shopee.controller.vendor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/vendor/dashboard")
public class VendorHomeController {

    @GetMapping("")
    public String home(Model model) {
        return "vendor/dashboard";
    }
}
