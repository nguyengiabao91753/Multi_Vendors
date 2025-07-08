package com.example.shopee.controller.client;

import java.time.Duration;
import com.example.shopee.config.VNPayConfig;
import com.example.shopee.entity.*;
import com.example.shopee.enums.ReturnStatus;
import com.example.shopee.repository.*;
import com.example.shopee.service.CloudinaryService;
import com.example.shopee.service.EmailSenderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/client/order")
public class ClientOrderController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CloudinaryService cloudinaryService;
    private Long id = 0L;
    @Autowired
    private ReturnRepository returnRepository;


    @GetMapping("")
    public String orderHistory(@RequestParam(name = "type", required = false) Integer type, Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return "redirect:/login";
        }

        UserEntity user = optionalUser.get();

        List<OrderEntity> orders;

        if (type == null || type == -99) {
            orders = new ArrayList<>(user.getOrderEntities());
        } else {
            Integer finalType = type;
            orders = user.getOrderEntities().stream()
                    .filter(order -> order.getStatus() != null && order.getStatus().equals(finalType))
                    .collect(Collectors.toList());
        }

        orders.sort(Comparator.comparing(OrderEntity::getCreatedAt).reversed());

        model.addAttribute("orders", orders);
        model.addAttribute("type", type == null ? -99 : type);

        return "history";
    }




    @GetMapping("/detail/{id}")
    public String orderDetail(@PathVariable Long id, Model model) {
        Optional<OrderEntity> orderOptional = orderRepository.findById(id);
        if (orderOptional.isEmpty()) {
            return "redirect:/client/order";
        }

        OrderEntity order = orderOptional.get();
        model.addAttribute("order", order);
        return "history-detail";
    }

    @GetMapping("/return/{id}")
    public String returnOrder(@PathVariable Long id, Model model) {
        Optional<OrderEntity> orderOptional = orderRepository.findById(id);
        this.id = id;
        if (orderOptional.isEmpty()) {
            return "redirect:/client/order";
        }

        OrderEntity order = orderOptional.get();
        model.addAttribute("order", order);

        LocalDateTime createdAt = order.getCreatedAt();
        LocalDateTime now = LocalDateTime.now();

        long daysBetween = Duration.between(createdAt, now).toDays();
        model.addAttribute("daysBetween", daysBetween);
        return "return";
    }



    @PostMapping("/return/save")
    public String saveReturnRequest(
            @RequestParam("reason") String reason,
            @RequestParam(value = "image", required = false) MultipartFile image,
            HttpSession session
    ) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        Optional<OrderEntity> orderOpt = orderRepository.findById(this.id);

        if (userOpt.isEmpty() || orderOpt.isEmpty()) {
            return "redirect:/client/order";
        }

        ReturnEntity returnEntity = new ReturnEntity();
        returnEntity.setReason(reason);
        returnEntity.setReturnStatus(ReturnStatus.PENDING);
        returnEntity.setOrderEntity(orderOpt.get());
        returnEntity.setUserEntity(userOpt.get());
        returnEntity.setCreatedAt(LocalDateTime.now());

        if (!image.isEmpty() && image != null) {
            String img = cloudinaryService.uploadFile(image);
            returnEntity.setImgReturn(img);
        }

        returnRepository.save(returnEntity);

        OrderEntity orderEntity = orderRepository.findById(id).get();
        orderEntity.setStatus(3);
        orderRepository.save(orderEntity);

        session.setAttribute("mess", "Đã gửi yêu cầu trả hàng!");
        return "thankYou";
    }

    @GetMapping("/return/detail/{id}")
    public String returnDetail(@PathVariable("id") Long id, Model model) {
        OrderEntity orderEntity = orderRepository.findById(id).orElse(null);
        if (orderEntity == null) {
            return "redirect:/client/order";
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email).orElse(null);
        model.addAttribute("user", user);

        ReturnEntity returnEntity = returnRepository.findTopByOrderEntityIdOrderByIdDesc(id).orElse(null);
        if (returnEntity == null) {
            return "redirect:/client/order";
        }

        model.addAttribute("return", returnEntity);
        return "return-detail";
    }

//    RETURN ORDER DETAIL

    private Long detailId = 0L;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @GetMapping("/returnDetail/{id}")
    public String returnDetailOrder(@PathVariable Long id, Model model) {
        Optional<OrderDetailEntity> orderOptional = orderDetailRepository.findById(id);
        this.detailId = id;
        if (orderOptional.isEmpty()) {
            return "redirect:/client/order";
        }

        OrderDetailEntity orderDetailEntity = orderOptional.get();
        model.addAttribute("orderDetail", orderDetailEntity);

        LocalDateTime createdAt = orderDetailEntity.getCreatedAt();
        LocalDateTime now = LocalDateTime.now();

        long daysBetween = Duration.between(createdAt, now).toDays();
        model.addAttribute("daysBetween", daysBetween);
        return "returnDetail";
    }



    @PostMapping("/returnDetail/save")
    public String saveReturnDetailRequest(
            @RequestParam("reason") String reason,
            @RequestParam(value = "image", required = false) MultipartFile image,
            HttpSession session
    ) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        Optional<OrderDetailEntity> orderOpt = orderDetailRepository.findById(this.detailId);

        if (userOpt.isEmpty() || orderOpt.isEmpty()) {
            return "redirect:/client/order";
        }

        ReturnEntity returnEntity = new ReturnEntity();
        returnEntity.setReason(reason);
        returnEntity.setReturnStatus(ReturnStatus.PENDING);
        returnEntity.setOrderDetailEntity(orderOpt.get());
        returnEntity.setUserEntity(userOpt.get());
        returnEntity.setCreatedAt(LocalDateTime.now());

        if (!image.isEmpty() && image != null) {
            String img = cloudinaryService.uploadFile(image);
            returnEntity.setImgReturn(img);
        }

        returnRepository.save(returnEntity);

        OrderDetailEntity detailEntity = orderDetailRepository.findById(this.detailId).get();
        detailEntity.setStatus(3);

        orderDetailRepository.save(detailEntity);

        session.setAttribute("mess", "Đã gửi yêu cầu trả hàng!");
        return "thankYou";
    }

    @GetMapping("/returnDetail/detail/{id}")
    public String returnDetailDetail(@PathVariable("id") Long id, Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email).orElse(null);
        model.addAttribute("user", user);

        ReturnEntity returnEntity = returnRepository.findTopByOrderDetailEntityIdOrderByIdDesc(id).orElse(null);
        if (returnEntity == null) {
            return "redirect:/client/order";
        }

        model.addAttribute("return", returnEntity);
        return "returnDetail-detail";
    }
}
