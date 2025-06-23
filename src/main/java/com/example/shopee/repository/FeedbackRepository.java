package com.example.shopee.repository;

import com.example.shopee.entity.CartDetailEntity;
import com.example.shopee.entity.FeedbackEntity;
import com.example.shopee.entity.ProductEntity;
import com.example.shopee.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<FeedbackEntity, Long> {
    @Query("SELECT f FROM FeedbackEntity f WHERE f.productEntity.user = :user")
    List<FeedbackEntity> findAllByProductOwner(@Param("user") UserEntity user);

    boolean existsByUserEntityAndProductEntity(UserEntity user, ProductEntity product);

    List<FeedbackEntity> findByProductEntityOrderByCreatedAtDesc(ProductEntity product);

    @Query("SELECT COUNT(f) FROM FeedbackEntity f WHERE f.productEntity.user.id = :vendorId")
    int countByVendorId(@Param("vendorId") Long vendorId);

}