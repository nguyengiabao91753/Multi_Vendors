package com.example.shopee.repository;

import com.example.shopee.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    List<ProductEntity> findAllByUserId(Long id);

    Page<ProductEntity> findAll(Pageable pageable);

    Page<ProductEntity> findAllByStatus(int status, Pageable pageable);

    @Query("SELECT p FROM ProductEntity p " +
            "WHERE p.status = 1 AND " +
            "(" +
            "   LOWER(p.productName) LIKE %:keyword% " +
            "   OR LOWER(p.description) LIKE %:keyword% " +
            "   OR EXISTS (SELECT c FROM p.categories c WHERE LOWER(c.categoryName) LIKE %:keyword%)" +
            ")")
    Page<ProductEntity> searchProducts(@Param("keyword") String keyword, Pageable pageable);
}