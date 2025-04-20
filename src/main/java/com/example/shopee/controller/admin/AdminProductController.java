package com.example.shopee.controller.admin;

import com.example.shopee.entity.CategoryEntity;
import com.example.shopee.entity.ProductEntity;
import com.example.shopee.entity.UserEntity;
import com.example.shopee.payload.dto.ProductDto;
import com.example.shopee.repository.CategoryRepository;
import com.example.shopee.repository.ProductRepository;
import com.example.shopee.repository.UserRepository;
import com.example.shopee.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/product")
public class AdminProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private UserRepository userRepository;

    // Hiển thị danh sách sản phẩm
    @GetMapping("")
    public String productPage(Model model) {
        List<ProductDto> products = productRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        model.addAttribute("products", products);
        return "admin/product/list";
    }

    @GetMapping("/insert")
    public String insertProductPage(Model model) {
        model.addAttribute("product", new ProductDto());
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/product/insert";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("product") ProductDto dto,
                       @RequestParam("file") MultipartFile file) {
        String urlImg = "";
        if (!file.isEmpty()) {
            urlImg = cloudinaryService.uploadFile(file);
        }
        ProductEntity entity = convertToEntity(dto);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email).get();
        entity.setUser(user);
        entity.setThumbnail(urlImg);
        productRepository.save(entity);
        return "redirect:/admin/product";
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable("id") Long id, Model model) {
        Optional<ProductEntity> optional = productRepository.findById(id);
        if (optional.isPresent()) {
            ProductDto dto = convertToDto(optional.get());
            model.addAttribute("product", dto);
            model.addAttribute("categories", categoryRepository.findAll());
            return "admin/product/update";
        } else {
            return "redirect:/admin/product";
        }
    }

    @PostMapping("/update")
    public String update(@ModelAttribute("product") ProductDto dto,
                         @RequestParam("file") MultipartFile file) {
        ProductEntity entity = productRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));

        UserEntity user = entity.getUser();

        if (!file.isEmpty()) {
            String urlImg = cloudinaryService.uploadFile(file);
            entity.setThumbnail(urlImg);
        } else {
            entity.setThumbnail(entity.getThumbnail());
        }

        entity.setProductName(dto.getProductName());
        entity.setDescription(dto.getDescription());
        entity.setAmount(dto.getAmount());
        entity.setPrice(dto.getPrice() != null ? dto.getPrice() : BigDecimal.ZERO);
        entity.setSalePrice(dto.getSalePrice() != null ? dto.getSalePrice() : BigDecimal.ZERO);
        entity.setStatus(dto.getStatus());

        if (dto.getCategoryId() != null) {
            Optional<CategoryEntity> category = categoryRepository.findById(dto.getCategoryId());
            category.ifPresent(entity::setCategoryEntity);
        }

        productRepository.save(entity);

        return "redirect:/admin/product/update/" + dto.getId();
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        productRepository.deleteById(id);
        return "redirect:/admin/product";
    }

    private ProductDto convertToDto(ProductEntity entity) {
        ProductDto dto = new ProductDto();
        dto.setId(entity.getId());
        dto.setProductName(entity.getProductName());
        dto.setDescription(entity.getDescription());
        dto.setAmount(entity.getAmount());
        dto.setPrice(entity.getPrice());
        dto.setSalePrice(entity.getSalePrice());
        dto.setThumbnail(entity.getThumbnail());
        dto.setStatus(entity.getStatus());
        dto.setEmail(entity.getUser().getEmail());

        dto.setCategoryId(entity.getCategoryEntity() != null ? entity.getCategoryEntity().getId() : null);
        dto.setCategoryName(entity.getCategoryEntity() != null ? entity.getCategoryEntity().getCategoryName() : "");
        return dto;
    }

    private ProductEntity convertToEntity(ProductDto dto) {
        ProductEntity entity = new ProductEntity();
        entity.setId(dto.getId());
        entity.setProductName(dto.getProductName());
        entity.setDescription(dto.getDescription());
        entity.setAmount(dto.getAmount());
        entity.setPrice(dto.getPrice() != null ? dto.getPrice() : BigDecimal.ZERO);
        entity.setSalePrice(dto.getSalePrice() != null ? dto.getSalePrice() : BigDecimal.ZERO);
        entity.setThumbnail(dto.getThumbnail());
        entity.setStatus(dto.getStatus());

        if (dto.getCategoryId() != null) {
            Optional<CategoryEntity> category = categoryRepository.findById(dto.getCategoryId());
            category.ifPresent(entity::setCategoryEntity);
        }

        return entity;
    }
}
