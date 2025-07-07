package com.example.shopee.controller.vendor;

import com.example.shopee.entity.CategoryEntity;
import com.example.shopee.entity.ProductEntity;
import com.example.shopee.entity.ProductImageEntity;
import com.example.shopee.entity.UserEntity;
import com.example.shopee.payload.dto.ProductDto;
import com.example.shopee.repository.CategoryRepository;
import com.example.shopee.repository.ProductImageRepository;
import com.example.shopee.repository.ProductRepository;
import com.example.shopee.repository.UserRepository;
import com.example.shopee.service.CloudinaryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/vendor/product")
public class VendorProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @GetMapping("")
    public String productPage(Model model,
                              @RequestParam(value = "categoryId", required = false) Long categoryId,
                              @RequestParam(value = "status", required = false) Integer status,
                              @RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "size", defaultValue = "5") int size) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email).get();


        List<ProductEntity> productEntities = productRepository.findAllByUserId(user.getId());

        if (categoryId != null) {
            productEntities = productEntities.stream()
                    .filter(p -> p.getCategories().stream().anyMatch(c -> c.getId().equals(categoryId)))
                    .collect(Collectors.toList());
        }

        if (status != null) {
            productEntities = productEntities.stream()
                    .filter(p -> p.getStatus() == status)
                    .collect(Collectors.toList());
        }

        int totalItems = productEntities.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        int start = page * size;
        int end = Math.min(start + size, totalItems);

        List<ProductDto> products = productEntities.subList(start, end).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        model.addAttribute("products", products);
        model.addAttribute("categories", categoryRepository.findAll());

        model.addAttribute("categoryId", categoryId);
        model.addAttribute("status", status);

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "vendor/product/list";
    }


    @GetMapping("/insert")
    public String insertProductPage(Model model) {
        model.addAttribute("product", new ProductDto());
        model.addAttribute("categories", categoryRepository.findAll());
        return "vendor/product/insert";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("product") ProductDto dto,
                       @ModelAttribute("listImage") MultipartFile[] listImage) {
        ProductEntity entity = convertToEntity(dto);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email).get();
        entity.setUser(user);
        entity.setStatus(0);
        entity.setMin(dto.getMin());

        List<CategoryEntity> categories = new ArrayList<>();
        if (dto.getCategoryIds() != null) {
            for (Long categoryId : dto.getCategoryIds()) {
                categoryRepository.findById(categoryId).ifPresent(categories::add);
            }
        }
        entity.setCategories(categories);

        ProductEntity save = productRepository.save(entity);

        if (listImage.length != 0) {
            for (MultipartFile y : listImage) {
                String urlImg = cloudinaryService.uploadFile(y);
                ProductImageEntity img = new ProductImageEntity();
                img.setProduct(save);
                img.setUrl_Image(urlImg);
                productImageRepository.save(img);
            }
        }

        return "redirect:/vendor/product?save=true";
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable("id") Long id, Model model) {
        Optional<ProductEntity> optional = productRepository.findById(id);
        if (optional.isPresent()) {
            ProductDto dto = convertToDto(optional.get());
            model.addAttribute("product", dto);
            model.addAttribute("categories", categoryRepository.findAll());
            return "vendor/product/update";
        } else {
            return "redirect:/vendor/product";
        }
    }

    @PostMapping("/update")
    public String update(@ModelAttribute("product") ProductDto dto,
                         @ModelAttribute("listImage") MultipartFile[] listImage) {
        ProductEntity entity = productRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        entity.setProductName(dto.getProductName());
        entity.setDescription(dto.getDescription());
        entity.setAmount(dto.getAmount());
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email).get();
        entity.setUser(user);
        entity.setMin(dto.getMin());
        entity.setPrice(dto.getPrice() != null ? dto.getPrice() : BigDecimal.ZERO);
        entity.setSalePrice(dto.getSalePrice() != null ? dto.getSalePrice() : BigDecimal.ZERO);

        List<CategoryEntity> categories = new ArrayList<>();
        if (dto.getCategoryIds() != null) {
            for (Long categoryId : dto.getCategoryIds()) {
                categoryRepository.findById(categoryId).ifPresent(categories::add);
            }
        }
        entity.setCategories(categories);

        ProductEntity save = productRepository.save(entity);

        if (listImage != null) {
            for (MultipartFile y : listImage) {
                if (!y.isEmpty()) {
                    String urlImg = cloudinaryService.uploadFile(y);
                    ProductImageEntity img = new ProductImageEntity();
                    img.setProduct(save);
                    img.setUrl_Image(urlImg);
                    productImageRepository.save(img);
                }
            }
        }

        productRepository.save(entity);

        return "redirect:/vendor/product?update=true";
    }


    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        productRepository.deleteById(id);
        return "redirect:/vendor/product?delete=true";
    }

    private ProductDto convertToDto(ProductEntity entity) {
        ProductDto dto = new ProductDto();
        dto.setId(entity.getId());
        dto.setProductName(entity.getProductName());
        dto.setDescription(entity.getDescription());
        dto.setAmount(entity.getAmount());
        dto.setPrice(entity.getPrice());
        dto.setSalePrice(entity.getSalePrice());
        dto.setMin(entity.getMin());
        dto.setStatus(entity.getStatus());
        dto.setEmail(entity.getUser().getEmail());
        dto.setProductImage(entity.getProductImage());
        if (entity.getCategories() != null) {
            List<Long> categoryIds = entity.getCategories()
                    .stream()
                    .map(CategoryEntity::getId)
                    .collect(Collectors.toList());
            dto.setCategoryIds(categoryIds);
        }

        if (entity.getAmount() != null && entity.getMin() != null) {
            dto.setNearOutOfStock(entity.getAmount() <= entity.getMin());
        } else {
            dto.setNearOutOfStock(false);
        }

        dto.setCategoryNames(entity.getCategories().stream()
                .map(CategoryEntity::getCategoryName)
                .collect(Collectors.toList()));

        dto.setImageUrls(entity.getProductImage().stream()
                .map(ProductImageEntity::getUrl_Image)
                .collect(Collectors.toList()));

        return dto;
    }

    private ProductEntity convertToEntity(ProductDto dto) {
        ProductEntity entity = new ProductEntity();
        entity.setId(dto.getId());
        entity.setProductName(dto.getProductName());
        entity.setDescription(dto.getDescription());
        entity.setAmount(dto.getAmount());
        entity.setMin(dto.getMin());
        entity.setPrice(dto.getPrice() != null ? dto.getPrice() : BigDecimal.ZERO);
        entity.setSalePrice(dto.getSalePrice() != null ? dto.getSalePrice() : BigDecimal.ZERO);
        entity.setStatus(dto.getStatus());
        entity.setProductImage(dto.getProductImage());

        if (dto.getCategoryIds() != null) {
            List<CategoryEntity> categories = categoryRepository.findAllById(dto.getCategoryIds());
            entity.setCategories(categories);
        }

        return entity;
    }

    @GetMapping("/delete-image/{id}")
    public String DeleteImage(@PathVariable Long id, HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        productImageRepository.deleteById(id);
        return "redirect:" + referer + "?delete=true";
    }

}
