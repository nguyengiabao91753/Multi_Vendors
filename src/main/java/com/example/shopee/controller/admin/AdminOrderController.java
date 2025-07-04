package com.example.shopee.controller.admin;

import com.example.shopee.entity.OrderEntity;
import com.example.shopee.entity.UserEntity;
import com.example.shopee.repository.OrderRepository;
import com.example.shopee.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/order")
public class AdminOrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("")
    public String orderPage(Model model,
                            @RequestParam(value = "status", required = false) Integer status,
                            @RequestParam(value = "page", defaultValue = "0") int page,
                            @RequestParam(value = "size", defaultValue = "5") int size) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email).orElse(null);
        model.addAttribute("user", user);
        if (user == null) {
            return "redirect:/login";
        }

        List<OrderEntity> orderEntities = orderRepository.findAll();

        if (status != null) {
            orderEntities = orderEntities.stream()
                    .filter(o -> o.getStatus() != null && o.getStatus() == status)
                    .collect(Collectors.toList());
        }

        model.addAttribute("orders", orderEntities);
        model.addAttribute("status", status);

        model.addAttribute("statusOptions", new Integer[]{-1, 0, 1, 2, 3});

        return "admin/order/list";
    }

    @GetMapping("/detail/{orderId}")
    public String orderDetail(@PathVariable("orderId") Long orderId, Model model) {
        OrderEntity order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return "redirect:/vendor/order";
        }

        model.addAttribute("order", order);

        return "admin/order/detail";
    }
}
