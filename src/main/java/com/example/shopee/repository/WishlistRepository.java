package com.example.shopee.repository;

import com.example.shopee.entity.ProductEntity;
import com.example.shopee.entity.UserEntity;
import com.example.shopee.entity.WishlistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<WishlistEntity, Long> {
    Optional<WishlistEntity> findByUserEntityAndProductEntity(UserEntity userEntity, ProductEntity productEntity);

    List<WishlistEntity> findAllByUserEntity(UserEntity userEntity);
}