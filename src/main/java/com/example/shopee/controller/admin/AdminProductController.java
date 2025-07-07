package com.example.shopee.controller.admin;

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
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
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
    private ProductImageRepository productImageRepository;

    @Autowired
    private UserRepository userRepository;

    // Hiển thị danh sách sản phẩm
    @GetMapping("")
    public String productPage(Model model,
                              @RequestParam(value = "categoryId", required = false) Long categoryId,
                              @RequestParam(value = "vendor", required = false) String vendor,
                              @RequestParam(value = "status", required = false) Integer status,
                              @RequestParam(value = "page", defaultValue = "0") int page, HttpSession session,
                              @RequestParam(value = "size", defaultValue = "5") int size) {

        String email = (String) session.getAttribute("email");

        if (email == null) {
            return "redirect:/admin/login";
        }



        List<ProductEntity> productEntities = productRepository.findAll();

        if (categoryId != null) {
            productEntities = productEntities.stream()
                    .filter(p -> p.getCategories().stream().anyMatch(c -> c.getId().equals(categoryId)))
                    .collect(Collectors.toList());
        }

        if (vendor != null && !vendor.isEmpty()) {
            productEntities = productEntities.stream()
                    .filter(p -> p.getUser().getEmail() != null && p.getUser().getEmail().toLowerCase().contains(vendor.toLowerCase()))
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
        model.addAttribute("vendor", vendor);
        model.addAttribute("status", status);

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "admin/product/list";
    }

    @PostMapping("/update-status/{id}")
    public String updateStatus(@PathVariable("id") Long id,
                               @RequestParam("status") Integer status, HttpSession session) {
        String email = (String) session.getAttribute("email");

        if (email == null) {
            return "redirect:/admin/login";
        }

        Optional<ProductEntity> optional = productRepository.findById(id);
        if (optional.isPresent()) {
            ProductEntity product = optional.get();
            product.setStatus(status);
            productRepository.save(product);
        }
        return "redirect:/admin/product";
    }


    @GetMapping("/insert")
    public String insertProductPage(Model model, HttpSession session) {
        String email = (String) session.getAttribute("email");

        if (email == null) {
            return "redirect:/admin/login";
        }

        model.addAttribute("product", new ProductDto());
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/product/insert";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("product") ProductDto dto,
                       @ModelAttribute("listImage") MultipartFile[] listImage, HttpSession session) {
        String email = (String) session.getAttribute("email");

        if (email == null) {
            return "redirect:/admin/login";
        }

        ProductEntity entity = convertToEntity(dto);

        UserEntity user = userRepository.findByEmail(email).get();
        entity.setUser(user);
        entity.setStatus(0);

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
        return "redirect:/admin/product?save=true";
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable("id") Long id, Model model, HttpSession session) {
        String email = (String) session.getAttribute("email");

        if (email == null) {
            return "redirect:/admin/login";
        }

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
                         @ModelAttribute("listImage") MultipartFile[] listImage, HttpSession session) {
        String email = (String) session.getAttribute("email");

        if (email == null) {
            return "redirect:/admin/login";
        }

        ProductEntity entity = productRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Product not exists"));

        entity.setProductName(dto.getProductName());
        entity.setDescription(dto.getDescription());
        entity.setAmount(dto.getAmount());

        UserEntity user = userRepository.findByEmail(email).get();
        entity.setUser(user);
        entity.setPrice(dto.getPrice() != null ? dto.getPrice() : BigDecimal.ZERO);
        entity.setSalePrice(dto.getSalePrice() != null ? dto.getSalePrice() : BigDecimal.ZERO);
        entity.setStatus(dto.getStatus());

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

        return "redirect:/admin/product?update=true";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, HttpSession session) {
        String email = (String) session.getAttribute("email");

        if (email == null) {
            return "redirect:/admin/login";
        }

        productRepository.deleteById(id);
        return "redirect:/admin/product?delete=true";
    }

    private ProductDto convertToDto(ProductEntity entity) {
        ProductDto dto = new ProductDto();
        dto.setId(entity.getId());
        dto.setProductName(entity.getProductName());
        dto.setDescription(entity.getDescription());
        dto.setAmount(entity.getAmount());
        dto.setPrice(entity.getPrice());
        dto.setSalePrice(entity.getSalePrice());
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
    public String DeleteImage(@PathVariable Long id, HttpServletRequest request, HttpSession session) {
        String email = (String) session.getAttribute("email");

        if (email == null) {
            return "redirect:/admin/login";
        }

        String referer = request.getHeader("Referer");
        productImageRepository.deleteById(id);
        return "redirect:" + referer + "?delete=true";
    }


}
