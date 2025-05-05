package com.example.shopee.payload.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Data
public class ReceiptDto {
    private Long id;
    private String supplier;
    private String createdAt;
    private BigDecimal totalCost;
    private Long userId;
    private String email;
    private List<ReceiptItemDto> items = new ArrayList<>();
}
