package com.example.shopee.repository;

import com.example.shopee.entity.OrderDetailEntity;
import com.example.shopee.entity.OrderEntity;
import com.example.shopee.entity.UserEntity;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
@SpringBootApplication
public interface OrderDetailRepository extends JpaRepository<OrderDetailEntity, Long> {

}
