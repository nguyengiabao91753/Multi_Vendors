package com.example.shopee.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "ProductVouchers")
public class ProductVoucherEntity extends AbstractEntity{

    @ManyToOne
    @JoinColumn(name = "product_id")
    @EqualsAndHashCode.Exclude
    private ProductEntity productEntity;

    @ManyToOne
    @JoinColumn(name = "voucher_id")
    @EqualsAndHashCode.Exclude
    private VoucherEntity voucherEntity;
}
