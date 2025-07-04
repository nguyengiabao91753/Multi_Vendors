package com.example.shopee.controller.vendor;

import com.example.shopee.entity.OrderEntity;
import com.example.shopee.entity.UserEntity;
import com.example.shopee.repository.OrderRepository;
import com.example.shopee.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/vendor/order")
public class VendorOrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("")
    public String orderPage(Model model,
                            @RequestParam(value = "status", required = false) Integer status) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email).orElse(null);
        model.addAttribute("user", user);
        if (user == null) {
            return "redirect:/login";
        }

        List<OrderEntity> allOrders = orderRepository.findAllByProductEntity_User(user);

        model.addAttribute("countPending", allOrders.stream().filter(o -> o.getStatus() == -1).count());
        model.addAttribute("countCancelled", allOrders.stream().filter(o -> o.getStatus() == 0).count());
        model.addAttribute("countDelivered", allOrders.stream().filter(o -> o.getStatus() == 1).count());
        model.addAttribute("countShipping", allOrders.stream().filter(o -> o.getStatus() == 2).count());
        model.addAttribute("countReturn", allOrders.stream().filter(o -> o.getStatus() == 3).count());

        List<OrderEntity> filteredOrders = allOrders;
        if (status != null) {
            filteredOrders = allOrders.stream()
                    .filter(o -> o.getStatus() != null && o.getStatus() == status)
                    .collect(Collectors.toList());
        }

        model.addAttribute("orders", filteredOrders);
        model.addAttribute("status", status);
        model.addAttribute("statusOptions", new Integer[]{-1, 0, 1, 2});

        return "vendor/order/list";
    }


    @GetMapping("/detail/{orderId}")
    public String orderDetail(@PathVariable("orderId") Long orderId, Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email).orElse(null);
        model.addAttribute("user", user);

        OrderEntity order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return "redirect:/vendor/order";
        }
        model.addAttribute("order", order);

        return "vendor/order/detail";
    }

    @PostMapping("/update-status/{orderId}")
    public String updateOrderStatus(@PathVariable("orderId") Long orderId,
                                    @RequestParam("status") Integer status) {
        OrderEntity order = orderRepository.findById(orderId).orElse(null);

        if (order != null) {
            order.setStatus(status);

            if (status == 1) {
                order.setPaymentStatus(1);
            }

            orderRepository.save(order);
        }

        return "redirect:/vendor/order";
    }

}
