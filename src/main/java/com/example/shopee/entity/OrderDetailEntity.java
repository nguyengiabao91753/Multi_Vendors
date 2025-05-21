package com.example.shopee.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "OrderDetails")
public class OrderDetailEntity extends AbstractEntity{
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
    @JoinColumn(name = "voucher_id")
    @EqualsAndHashCode.Exclude
    private VoucherEntity voucherEntity;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @EqualsAndHashCode.Exclude
    private OrderEntity orderEntity;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @EqualsAndHashCode.Exclude
    private ProductEntity productEntity;

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

    public VoucherEntity getVoucherEntity() {
        return voucherEntity;
    }

    public void setVoucherEntity(VoucherEntity voucherEntity) {
        this.voucherEntity = voucherEntity;
    }

    public OrderEntity getOrderEntity() {
        return orderEntity;
    }

    public void setOrderEntity(OrderEntity orderEntity) {
        this.orderEntity = orderEntity;
    }

    public ProductEntity getProductEntity() {
        return productEntity;
    }

    public void setProductEntity(ProductEntity productEntity) {
        this.productEntity = productEntity;
    }
}
