package com.example.shopee.repository;

import com.example.shopee.entity.ReceiptDetailEntity;
import com.example.shopee.entity.ReceiptEntity;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SpringBootApplication
public interface ReceiptDetailRepository extends JpaRepository<ReceiptDetailEntity, Long> {
    List<ReceiptDetailEntity> findAllByReceiptEntityId(Long id);
}