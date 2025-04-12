package com.example.shopee.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "Feedback",
        catalog = "")
@Data
public class FeedbackEntity extends AbstractEntity {

    @Basic
    @Column(name = "feedback_date_time", nullable = true)
    private Integer feedbackDateTime;


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
}
