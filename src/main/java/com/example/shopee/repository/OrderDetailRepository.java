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
import java.time.Year;
import java.util.List;

@Repository
@SpringBootApplication
public interface OrderDetailRepository extends JpaRepository<OrderDetailEntity, Long> {

    @Query("SELECT COUNT(od) > 0 FROM OrderDetailEntity od " +
            "WHERE od.productEntity.id = :productId AND od.orderEntity.userEntity.id = :userId")
    boolean hasUserBoughtProduct(@Param("productId") Long productId, @Param("userId") Long userId);

    @Query("SELECT SUM(od.totalPrice) FROM OrderDetailEntity od " +
            "WHERE FUNCTION('MONTH', od.orderEntity.createdAt) = :month " +
            "AND FUNCTION('YEAR', od.orderEntity.createdAt) = :year " +
            "AND od.productEntity.user.id = :vendorId")
    BigDecimal getMonthlyRevenueForVendor(@Param("month") int month,
                                          @Param("vendorId") Long vendorId,
                                          @Param("year") int year);

    @Query("SELECT SUM(od.totalPrice) FROM OrderDetailEntity od " +
            "WHERE FUNCTION('MONTH', od.orderEntity.createdAt) = :month " +
            "AND FUNCTION('YEAR', od.orderEntity.createdAt) = :year")
    BigDecimal getMonthlyRevenue(@Param("month") int month,
                                          @Param("year") int year);
}
