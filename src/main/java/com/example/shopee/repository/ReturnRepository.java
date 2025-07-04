package com.example.shopee.repository;

import com.example.shopee.entity.ReturnEntity;
import com.example.shopee.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReturnRepository extends JpaRepository<ReturnEntity, Long> {
    List<ReturnEntity> findAllByOrderEntity_ProductEntity_User(UserEntity user);
    Optional<ReturnEntity> findTopByOrderEntityIdOrderByIdDesc(Long orderId);
}
