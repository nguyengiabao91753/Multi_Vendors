package com.example.shopee.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@Table(name = "Receipts")
public class ReceiptEntity extends AbstractEntity{

    private String supplier;

    @Basic
    @Column(name = "total_cost", nullable = true)
    private BigDecimal totalCost;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @EqualsAndHashCode.Exclude
    private UserEntity userEntity;

    @OneToMany(mappedBy = "receiptEntity", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<ReceiptDetailEntity> receiptDetailEntities;

}
