package com.example.shopee.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Objects;

@Data
@Entity
@Table(name = "CartDetails")
public class CartDetailEntity extends AbstractEntity{
    @Basic
    @Column(name = "quantity", nullable = true)
    private Integer quantity;

    @Basic
    @Column(name = "price_of_one", nullable = true)
    private BigDecimal priceOfOne;

    @Basic
    @Column(name = "total_price", nullable = true)
    private BigDecimal totalPrice;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private CartEntity cartEntity;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private ProductEntity productEntity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CartDetailEntity that = (CartDetailEntity) o;
        return Objects.equals(quantity, that.quantity) && Objects.equals(priceOfOne, that.priceOfOne) && Objects.equals(totalPrice, that.totalPrice) && Objects.equals(cartEntity, that.cartEntity) && Objects.equals(productEntity, that.productEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), quantity, priceOfOne, totalPrice, cartEntity, productEntity);
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPriceOfOne() {
        return priceOfOne;
    }

    public void setPriceOfOne(BigDecimal priceOfOne) {
        this.priceOfOne = priceOfOne;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public CartEntity getCartEntity() {
        return cartEntity;
    }

    public void setCartEntity(CartEntity cartEntity) {
        this.cartEntity = cartEntity;
    }

    public ProductEntity getProductEntity() {
        return productEntity;
    }

    public void setProductEntity(ProductEntity productEntity) {
        this.productEntity = productEntity;
    }
}
