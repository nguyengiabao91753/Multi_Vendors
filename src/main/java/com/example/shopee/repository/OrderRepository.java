package com.example.shopee.repository;

import com.example.shopee.entity.CategoryEntity;
import com.example.shopee.entity.OrderDetailEntity;
import com.example.shopee.entity.OrderEntity;
import com.example.shopee.entity.UserEntity;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@SpringBootApplication
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByUserEntityId(Long userId);

    @Query("SELECT od FROM OrderDetailEntity od join ProductEntity p on od.productEntity.id = p.id WHERE p.user.id = :userId AND od.orderEntity.status = 1")
    List<OrderDetailEntity> findAllCompletedOrders(Long userId);

    List<OrderEntity> findAllByOrderDetailEntities_ProductEntity_User(UserEntity userEntity);
}
