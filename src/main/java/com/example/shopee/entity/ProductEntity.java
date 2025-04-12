package com.example.shopee.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Entity
@Table(name = "Products")
public class ProductEntity extends AbstractEntity{
    @Basic
    @Column(length = 255,name = "product_name", nullable = true,  columnDefinition = "nvarchar(255)")
    private String productName;

    @Basic
    @Column(name = "thumbnail", nullable = true, length = 255, columnDefinition = "nvarchar(255)")
    private String thumbnail;

    @Basic
    @Column(name = "description", nullable = true, length = 255, columnDefinition = "nvarchar(255)")
    private String description;

    @Basic
    @Column(name = "amount", nullable = true)
    private Integer amount;

    @Basic
    @Column(name = "price", nullable = true)
    private BigDecimal price;

    @Basic
    @Column(name = "sale_price", nullable = true)
    private BigDecimal salePrice;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @EqualsAndHashCode.Exclude
    private CategoryEntity categoryEntity;

    @OneToMany(mappedBy = "productEntity", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<FeedbackEntity> feedbackEntities;

    @OneToMany(mappedBy = "productEntity", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<ProductVoucherEntity> productVoucherEntities;

    @OneToMany(mappedBy = "productEntity", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<OrderDetailEntity> orderDetailEntities;



}
