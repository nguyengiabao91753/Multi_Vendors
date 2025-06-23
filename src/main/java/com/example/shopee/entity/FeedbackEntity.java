package com.example.shopee.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "Feedback",
        catalog = "")
@Data
public class FeedbackEntity extends AbstractEntity {
    @Basic
    @Column(name = "rated_star", nullable = true)
    private Integer ratedStar;

    @Basic
    @Column(name = "comment", nullable = true, length = 255, columnDefinition = "nvarchar(255)")
    private String comment;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @EqualsAndHashCode.Exclude
    private UserEntity userEntity;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @EqualsAndHashCode.Exclude
    private ProductEntity productEntity;

    public Integer getRatedStar() {
        return ratedStar;
    }

    public void setRatedStar(Integer ratedStar) {
        this.ratedStar = ratedStar;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public ProductEntity getProductEntity() {
        return productEntity;
    }

    public void setProductEntity(ProductEntity productEntity) {
        this.productEntity = productEntity;
    }
}
