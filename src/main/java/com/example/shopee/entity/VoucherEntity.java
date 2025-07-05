package com.example.shopee.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "Vouchers")
@Data
public class VoucherEntity extends AbstractEntity {
    @Basic
    @Column(name = "name", nullable = true, length = 255, columnDefinition = "nvarchar(255)")
    private String name;

    @Basic
    @Column(name = "code", nullable = true, length = 255)
    private String code;

    @Basic
    @Column(name = "amount", nullable = true)
    private Integer amount;

    @Basic
    @Column(name = "percent_decrease", nullable = true)
    private Integer percentDecrease;

    @Basic
    @Column(name = "start_time", nullable = true)
    private LocalDateTime startTime;

    @OneToMany(mappedBy = "voucherEntity", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private Set<OrderEntity> orders;

    @Basic
    @Column(name = "end_time", nullable = true)
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private UserEntity userEntity;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getPercentDecrease() {
        return percentDecrease;
    }

    public void setPercentDecrease(Integer percentDecrease) {
        this.percentDecrease = percentDecrease;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }
}
