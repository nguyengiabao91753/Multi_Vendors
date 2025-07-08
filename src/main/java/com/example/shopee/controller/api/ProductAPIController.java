package com.example.shopee.controller.api;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.web.bind.annotation.*;

import com.example.shopee.entity.CategoryEntity;
import com.example.shopee.entity.ProductEntity;
import com.example.shopee.entity.ProductImageEntity;
import com.example.shopee.payload.dto.ProductDto;
import com.example.shopee.repository.ProductRepository;

@RestController
@RequestMapping("/api/product")
public class ProductAPIController {
	@Autowired
    private ProductRepository productRepository;


	@GetMapping("/active")
	public Page<ProductEntity> getActiveProducts(
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size
	) {
	    Pageable pageable = PageRequest.of(page, size);
	    return productRepository.findAllByStatus(1, pageable);
	}
	
	@GetMapping
    public Page<ProductDto> getPaginatedProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductEntity> entities = productRepository.findAllByStatus(1, pageable);
        List<ProductDto> dtos = entities.stream().map(this::convertToDto).toList();
        return new PageImpl<>(dtos, pageable, entities.getTotalElements());
    }


    // ✅ Get Product by ID
    @GetMapping("/{id}")
    public ProductEntity getById(@PathVariable Long id) {
        Optional<ProductEntity> optional = productRepository.findById(id);
        return optional.orElseThrow(() -> new RuntimeException("Product not found"));
    }

    // ✅ Search by keyword (name, description, category)
    @GetMapping("/search")
    public Page<ProductDto> searchByKeyword(
    		@RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        var entities= productRepository.searchProducts(keyword.toLowerCase(), pageable);
        List<ProductDto> dtos = entities.stream().map(this::convertToDto).toList();
        return new PageImpl<>(dtos, pageable, entities.getTotalElements());
    }
    
    
    @GetMapping("/best-selling")
    public List<ProductDto> getBestSellingProducts() {
        List<ProductEntity> bestSelling = productRepository.findBestSellingProducts();
        return bestSelling.stream().limit(5)
        		.map(this::convertToDto)
                .collect(Collectors.toList()); // chỉ lấy 5 sản phẩm đầu
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
}
