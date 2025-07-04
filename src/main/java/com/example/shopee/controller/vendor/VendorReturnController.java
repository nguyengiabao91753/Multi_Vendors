package com.example.shopee.controller.vendor;

import com.example.shopee.entity.OrderEntity;
import com.example.shopee.entity.ProductEntity;
import com.example.shopee.entity.ReturnEntity;
import com.example.shopee.entity.UserEntity;
import com.example.shopee.enums.ReturnStatus;
import com.example.shopee.repository.OrderRepository;
import com.example.shopee.repository.ProductRepository;
import com.example.shopee.repository.ReturnRepository;
import com.example.shopee.repository.UserRepository;
import com.example.shopee.service.CloudinaryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/vendor/return")
public class VendorReturnController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReturnRepository returnRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private ProductRepository productRepository;

    private Long id = 0L;

    @GetMapping("")
    public String orderPage(Model model) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email).orElse(null);
        model.addAttribute("user", user);
        if (user == null) {
            return "redirect:/login";
        }

        List<ReturnEntity> allOrders = returnRepository.findAllByOrderEntity_ProductEntity_User(user);
        model.addAttribute("returns", allOrders);

        return "vendor/return/list";
    }

    @GetMapping("/detail/{id}")
    public String orderDetail(@PathVariable("id") Long id, Model model) {
        this.id = id;
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email).orElse(null);
        model.addAttribute("user", user);
        ReturnEntity returnEntity = returnRepository.findById(id).orElse(null);
        model.addAttribute("return", returnEntity);
        return "vendor/return/detail";
    }

    @PostMapping("/updateStatus")
    public String updateReturnStatus(
            @RequestParam("id") Long id,
            @RequestParam("action") String action,
            HttpSession session
    ) {
        Optional<ReturnEntity> returnOpt = returnRepository.findById(id);
        if (returnOpt.isEmpty()) {
            session.setAttribute("mess", "Không tìm thấy yêu cầu trả hàng!");
            return "redirect:/vendor/return";
        }

        ReturnEntity returnEntity = returnOpt.get();

        try {
            ReturnStatus newStatus = ReturnStatus.valueOf(action);
            returnEntity.setReturnStatus(newStatus);
            returnRepository.save(returnEntity);

            if (newStatus == ReturnStatus.COMPLETED) {
                OrderEntity order = returnEntity.getOrderEntity();
                ProductEntity product = order.getProductEntity();

                if (product != null && order.getQuantity() != null) {
                    Integer currentAmount = product.getAmount() != null ? product.getAmount() : 0;
                    product.setAmount(currentAmount + order.getQuantity());
                    productRepository.save(product);
                }
            }


            session.setAttribute("mess", "Cập nhật trạng thái thành công!");
        } catch (IllegalArgumentException e) {
            session.setAttribute("mess", "Trạng thái không hợp lệ!");
        }

        return "redirect:/vendor/return/detail/" + id;
    }


    @PostMapping("/save")
    public String saveReturnRequest(
            @RequestParam("note") String note,
            @RequestParam(value = "image", required = false) MultipartFile image,
            HttpSession session
    ) {
        ReturnEntity returnEntity = returnRepository.findById(this.id).get();
        returnEntity.setNote(note);

        if (!image.isEmpty() && image != null) {
            String img = cloudinaryService.uploadFile(image);
            returnEntity.setImgBack(img);
        }

        returnRepository.save(returnEntity);

        session.setAttribute("mess", "Đã lưu thông tin!");
        return "redirect:/vendor/return/detail/" + this.id;
    }

}
