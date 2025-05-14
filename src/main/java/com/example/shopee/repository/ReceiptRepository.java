package com.example.shopee.repository;

import com.example.shopee.entity.ProductEntity;
import com.example.shopee.entity.ReceiptEntity;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SpringBootApplication
public interface ReceiptRepository extends JpaRepository<ReceiptEntity, Long> {
    List<ReceiptEntity> findAllByUserEntityId(Long id);

    Page<ReceiptEntity> findAllByUserEntityId(Long userId, Pageable pageable);

}
