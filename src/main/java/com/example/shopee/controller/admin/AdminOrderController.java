package com.example.shopee.controller.admin;

import com.example.shopee.entity.OrderEntity;
import com.example.shopee.entity.UserEntity;
import com.example.shopee.repository.OrderRepository;
import com.example.shopee.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/order")
public class AdminOrderController {

    @Autowired
    private OrderRepository orderRepository;


    @GetMapping("")
    public String orderPage(Model model,
                            @RequestParam(value = "status", required = false) Integer status) {
        List<OrderEntity> ordersWithProduct = orderRepository.findAll();
        List<OrderEntity> ordersWithDetails = orderRepository.findDistinctByOrderDetailEntities_ProductIsNotNull();

        Map<Long, OrderEntity> mergedOrdersMap = new LinkedHashMap<>();
        for (OrderEntity order : ordersWithProduct) {
            mergedOrdersMap.put(order.getId(), order);
        }
        for (OrderEntity order : ordersWithDetails) {
            mergedOrdersMap.putIfAbsent(order.getId(), order);
        }

        List<OrderEntity> allOrders = new ArrayList<>(mergedOrdersMap.values());

        model.addAttribute("countPending", allOrders.stream().filter(o -> o.getStatus() == -1).count());
        model.addAttribute("countCancelled", allOrders.stream().filter(o -> o.getStatus() == 0).count());
        model.addAttribute("countDelivered", allOrders.stream().filter(o -> o.getStatus() == 1).count());
        model.addAttribute("countShipping", allOrders.stream().filter(o -> o.getStatus() == 2).count());
        model.addAttribute("countReturn", allOrders.stream().filter(o -> o.getStatus() == 3).count());

        List<OrderEntity> filteredOrders = allOrders;
        if (status != null) {
            filteredOrders = allOrders.stream()
                    .filter(o -> o.getStatus() != null && o.getStatus().equals(status))
                    .collect(Collectors.toList());
        }

        model.addAttribute("orders", filteredOrders);
        model.addAttribute("status", status);
        model.addAttribute("statusOptions", new Integer[]{-1, 0, 1, 2, 3});

        return "admin/order/list";
    }

    @GetMapping("/detail/{orderId}")
    public String orderDetail(@PathVariable("orderId") Long orderId, Model model, HttpSession session) {
        String email = (String) session.getAttribute("email");

        if (email == null) {
            return "redirect:/admin/login";
        }
        OrderEntity order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return "redirect:/admin/order";
        }

        model.addAttribute("order", order);

        return "admin/order/detail";
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

        return "redirect:/admin/order?update=true";
    }


}
