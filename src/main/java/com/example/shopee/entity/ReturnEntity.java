package com.example.shopee.entity;

import com.example.shopee.enums.ReturnStatus;
import com.example.shopee.enums.RoleEnum;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@Table(name = "Returns")
public class ReturnEntity extends AbstractEntity{
    private String reason; // Lí do hoàn hàng
    private String note; //Lí do từ chối...

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private ReturnStatus returnStatus;

    private String imgReturn; // Ảnh của người mua khi nhận hàng từ shipper
    private String imgBack; // Ảnh của người bán khi nhận lại

    @OneToOne
    @JoinColumn(name = "order_id")
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private OrderEntity orderEntity;

    @OneToOne
    @JoinColumn(name = "order_detail_id")
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private OrderDetailEntity orderDetailEntity;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private UserEntity userEntity;

    public OrderDetailEntity getOrderDetailEntity() {
        return orderDetailEntity;
    }

    public void setOrderDetailEntity(OrderDetailEntity orderDetailEntity) {
        this.orderDetailEntity = orderDetailEntity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public ReturnStatus getReturnStatus() {
        return returnStatus;
    }

    public void setReturnStatus(ReturnStatus returnStatus) {
        this.returnStatus = returnStatus;
    }

    public String getImgReturn() {
        return imgReturn;
    }

    public void setImgReturn(String imgReturn) {
        this.imgReturn = imgReturn;
    }

    public String getImgBack() {
        return imgBack;
    }

    public void setImgBack(String imgBack) {
        this.imgBack = imgBack;
    }

    public OrderEntity getOrderEntity() {
        return orderEntity;
    }

    public void setOrderEntity(OrderEntity orderEntity) {
        this.orderEntity = orderEntity;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }
}
