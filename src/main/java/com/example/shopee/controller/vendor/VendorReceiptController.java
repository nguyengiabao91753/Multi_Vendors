package com.example.shopee.controller.vendor;

import com.example.shopee.entity.ProductEntity;
import com.example.shopee.entity.ReceiptDetailEntity;
import com.example.shopee.entity.ReceiptEntity;
import com.example.shopee.entity.UserEntity;
import com.example.shopee.payload.dto.ReceiptDto;
import com.example.shopee.payload.dto.ReceiptItemDto;
import com.example.shopee.repository.ProductRepository;
import com.example.shopee.repository.ReceiptDetailRepository;
import com.example.shopee.repository.ReceiptRepository;
import com.example.shopee.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/vendor/receipt")
public class VendorReceiptController {

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private ReceiptDetailRepository receiptDetailRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("")
    public String showReceiptList(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) return "redirect:/login";
        UserEntity user = optionalUser.get();

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<ReceiptEntity> receiptPage;

        receiptPage = receiptRepository.findAllByUserEntityId(user.getId(), pageable);

        List<ReceiptDto> receiptDtos = receiptPage.getContent()
                .stream().map(this::convertToDto)
                .collect(Collectors.toList());

        model.addAttribute("receipts", receiptDtos);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", receiptPage.getTotalPages());

        return "vendor/receipt/list";
    }


    @GetMapping("/insert")
    public String showInsertForm(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) return "redirect:/login";

        UserEntity user = optionalUser.get();

        List<ProductEntity> products = productRepository.findAllByUserId(user.getId());

        model.addAttribute("receipt", new ReceiptDto());
        model.addAttribute("products", products);
        System.out.println(products);

        return "vendor/receipt/insert";
    }

    @PostMapping("/save")
    public String saveReceipt(@ModelAttribute("receipt") ReceiptDto dto) {
        System.out.println(dto);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) return "redirect:/login";

        UserEntity user = optionalUser.get();

        ReceiptEntity receipt = new ReceiptEntity();
        receipt.setSupplier(dto.getSupplier());
        receipt.setCreatedAt(LocalDateTime.now());
        receipt.setUserEntity(user);

        BigDecimal totalCost = BigDecimal.ZERO;

        ReceiptEntity savedReceipt = receiptRepository.save(receipt);

        if (dto.getItems() != null) {
            for (ReceiptItemDto itemDto : dto.getItems()) {
                Optional<ProductEntity> optionalProduct = productRepository.findById(itemDto.getProductId());

                if (optionalProduct.isEmpty()) continue;

                ProductEntity product = optionalProduct.get();
                BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity()));
                totalCost = totalCost.add(lineTotal);

                ReceiptDetailEntity detail = new ReceiptDetailEntity();
                detail.setReceiptEntity(savedReceipt);
                detail.setProductEntity(product);
                detail.setQuantity(itemDto.getQuantity());

                receiptDetailRepository.save(detail);
            }
        }

        savedReceipt.setTotalCost(totalCost);
        receiptRepository.save(savedReceipt);

        return "redirect:/vendor/receipt";
    }

    @GetMapping("/detail/{id}")
    public String showDetail(@PathVariable("id") Long id, Model model) {
        Optional<ReceiptEntity> optional = receiptRepository.findById(id);

        if (optional.isEmpty()) {
            return "redirect:/vendor/receipt";
        }

        ReceiptDto dto = convertToDto(optional.get());
        model.addAttribute("receipt", dto);
        return "vendor/receipt/detail";
    }

    @GetMapping("/delete/{id}")
    public String deleteReceipt(@PathVariable("id") Long id) {
        receiptRepository.deleteById(id);
        return "redirect:/vendor/receipt";
    }

    private ReceiptDto convertToDto(ReceiptEntity entity) {
        ReceiptDto dto = new ReceiptDto();
        dto.setId(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt().toString());
        dto.setTotalCost(entity.getTotalCost());
        dto.setSupplier(entity.getSupplier());
        dto.setEmail(entity.getUserEntity().getEmail());

        List<ReceiptDetailEntity> details = receiptDetailRepository.findAllByReceiptEntityId(entity.getId());

        List<ReceiptItemDto> itemDtos = details.stream().map(detail -> {
            ReceiptItemDto itemDto = new ReceiptItemDto();
            itemDto.setProductId(detail.getProductEntity().getId());
            itemDto.setProductName(detail.getProductEntity().getProductName());
            itemDto.setPrice(detail.getProductEntity().getPrice());
            itemDto.setQuantity(detail.getQuantity());
            return itemDto;
        }).collect(Collectors.toList());

        dto.setItems(itemDtos);

        return dto;
    }

}
