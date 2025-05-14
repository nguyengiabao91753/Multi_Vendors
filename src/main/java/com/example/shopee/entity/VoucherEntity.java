package com.example.shopee.entity;

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

    @Basic
    @Column(name = "end_time", nullable = true)
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @EqualsAndHashCode.Exclude
    private UserEntity userEntity;
}
