package com.example.shopee.repository;

import com.example.shopee.entity.ProductEntity;
import com.example.shopee.entity.ProductImageEntity;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SpringBootApplication
public interface ProductImageRepository extends JpaRepository<ProductImageEntity, Long> {
}
