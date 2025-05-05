package com.example.shopee.payload.dto;

import com.example.shopee.entity.ProductImageEntity;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductDto {
    private Long id;
    private String productName;
    private String description;
    private Integer amount;
    private BigDecimal price;
    private BigDecimal salePrice;
    private String email; // dùng để map với CategoryEntity
    private Integer status;
    private List<Long> categoryIds; // mới
    private List<ProductImageEntity> productImage; // mới
    private List<String> categoryNames;
    private List<String> imageUrls;

}
