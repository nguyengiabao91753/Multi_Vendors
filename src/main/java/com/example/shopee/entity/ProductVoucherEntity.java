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

    public ProductEntity getProductEntity() {
        return productEntity;
    }

    public void setProductEntity(ProductEntity productEntity) {
        this.productEntity = productEntity;
    }

    public VoucherEntity getVoucherEntity() {
        return voucherEntity;
    }

    public void setVoucherEntity(VoucherEntity voucherEntity) {
        this.voucherEntity = voucherEntity;
    }
}
