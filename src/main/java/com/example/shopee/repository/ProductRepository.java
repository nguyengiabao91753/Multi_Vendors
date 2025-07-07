package com.example.shopee.repository;

import com.example.shopee.entity.CategoryEntity;
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

    List<ProductEntity> findAllByStatus(int status);
    Page<ProductEntity> findAllByUserIdAndStatus(Long id, int status, Pageable pageable);

    @Query("SELECT p FROM ProductEntity p " +
            "WHERE p.status = 1 AND " +
            "(" +
            "   LOWER(p.productName) LIKE %:keyword% " +
            "   OR LOWER(p.description) LIKE %:keyword% " +
            "   OR EXISTS (SELECT c FROM p.categories c WHERE LOWER(c.categoryName) LIKE %:keyword%)" +
            ")")
    Page<ProductEntity> searchProducts(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT COUNT(p) FROM ProductEntity p WHERE p.user.id = :userId")
    int countByUserId(@Param("userId") Long userId);

    Page<ProductEntity> findByCategoriesAndStatus(CategoryEntity category, Integer status, Pageable pageable);

    @Query("SELECT od.productEntity FROM OrderEntity od " +
            "GROUP BY od.productEntity " +
            "ORDER BY SUM(od.quantity) DESC")
    List<ProductEntity> findBestSellingProducts();

    @Query("SELECT w.productEntity FROM WishlistEntity w " +
            "GROUP BY w.productEntity " +
            "ORDER BY COUNT(w.id) DESC")
    List<ProductEntity> findMostLikedProducts();
}