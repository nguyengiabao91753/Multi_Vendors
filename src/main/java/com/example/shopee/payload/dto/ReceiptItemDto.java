package com.example.shopee.payload.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
public class ReceiptItemDto {
    private Long productId;
    private Integer quantity;
    private String productName;
    private BigDecimal price;
}
