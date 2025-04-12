package com.example.shopee.controller.client;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/client/home")
public class ClientHomeController {

    @GetMapping("")
    public String home(Model model) {
        return "client/index";
    }
}
