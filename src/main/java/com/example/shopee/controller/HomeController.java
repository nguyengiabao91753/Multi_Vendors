package com.example.shopee.controller;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("")
public class HomeController {

    @GetMapping("")
    private String indexHome(Model model) {
        return "redirect:/index";
    }

    @GetMapping("/home")
    private String homePage(Model model) {
        return "redirect:/index";
    }

    @GetMapping("/index")
    private String index(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if (authorities.stream().anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()))) {
            model.addAttribute("email", SecurityContextHolder.getContext().getAuthentication().getName());
            return "redirect:/admin/dashboard";
        } else if (authorities.stream().anyMatch(authority -> "ROLE_VENDOR".equals(authority.getAuthority()))) {
            model.addAttribute("email", SecurityContextHolder.getContext().getAuthentication().getName());
            return "redirect:/vendor/dashboard";
        } else {
            if (!Objects.equals(username, "")) {
                model.addAttribute("email", SecurityContextHolder.getContext().getAuthentication().getName());
                return "redirect:/client/home";
            } else {
                return "index";
            }
        }
    }


}
