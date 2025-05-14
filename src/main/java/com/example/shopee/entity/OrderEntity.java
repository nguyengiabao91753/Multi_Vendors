package com.example.shopee.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@Table(name = "Orders")
public class OrderEntity extends AbstractEntity{
    @Basic
    @Column(name = "order_time", nullable = true)
    private LocalDateTime orderTime;

    @Basic
    @Column(name = "total_cost", nullable = true)
    private BigDecimal totalCost;

    @Basic
    @Column(name = "shipping_fee", nullable = true)
    private BigDecimal shippingFee;

    @Basic
    @Column(name = "full_cost", nullable = true)
    private BigDecimal fullCost;

    @OneToMany(mappedBy = "orderEntity", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<OrderDetailEntity> orderDetailEntities;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @EqualsAndHashCode.Exclude
    private UserEntity userEntity;


}
