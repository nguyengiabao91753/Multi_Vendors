package com.example.shopee.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "Products")
public class ProductEntity extends AbstractEntity{
    @Basic
    @Column(length = 255,name = "product_name", nullable = true,  columnDefinition = "nvarchar(255)")
    private String productName;

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

    @ManyToMany
    @JoinTable(
            name = "product_category",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private List<CategoryEntity> categories;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ProductImageEntity> productImage;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private UserEntity user;

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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public List<CategoryEntity> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryEntity> categories) {
        this.categories = categories;
    }

    public List<ProductImageEntity> getProductImage() {
        return productImage;
    }

    public void setProductImage(List<ProductImageEntity> productImage) {
        this.productImage = productImage;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Set<FeedbackEntity> getFeedbackEntities() {
        return feedbackEntities;
    }

    public void setFeedbackEntities(Set<FeedbackEntity> feedbackEntities) {
        this.feedbackEntities = feedbackEntities;
    }

    public Set<ProductVoucherEntity> getProductVoucherEntities() {
        return productVoucherEntities;
    }

    public void setProductVoucherEntities(Set<ProductVoucherEntity> productVoucherEntities) {
        this.productVoucherEntities = productVoucherEntities;
    }

    public Set<OrderDetailEntity> getOrderDetailEntities() {
        return orderDetailEntities;
    }

    public void setOrderDetailEntities(Set<OrderDetailEntity> orderDetailEntities) {
        this.orderDetailEntities = orderDetailEntities;
    }
}
