package com.example.shopee.entity;

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
    private CartEntity cartEntity;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @EqualsAndHashCode.Exclude
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
}
