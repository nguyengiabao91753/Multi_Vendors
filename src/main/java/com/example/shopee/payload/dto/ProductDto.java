package com.example.shopee.payload.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductDto {
    private Long id;
    private String productName;
    private String thumbnail;
    private String description;
    private Integer amount;
    private BigDecimal price;
    private BigDecimal salePrice;
    private Long categoryId; // dùng để map với CategoryEntity
    private String categoryName; // dùng để map với CategoryEntity
    private String email; // dùng để map với CategoryEntity
    private Integer status;

}
