package com.example.shopee.repository;

import com.example.shopee.entity.OrderEntity;
import com.example.shopee.entity.UserEntity;
import org.hibernate.query.Order;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
@SpringBootApplication
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByUserEntityId(Long userId);

    @Query("SELECT od FROM OrderEntity od join ProductEntity p on od.productEntity.id = p.id WHERE p.user.id = :userId AND od.status = 1")
    List<OrderEntity> findAllCompletedOrders(Long userId);

    List<OrderEntity> findAllByProductEntity_User(UserEntity userEntity);

    @Query("SELECT COUNT(o) > 0 FROM OrderEntity o " +
            "WHERE o.productEntity.id = :productId AND o.userEntity.id = :userId")
    boolean hasUserBoughtProduct(@Param("productId") Long productId, @Param("userId") Long userId);

    @Query("SELECT SUM(od.totalCost) FROM OrderEntity od " +
            "WHERE FUNCTION('MONTH', od.createdAt) = :month " +
            "AND FUNCTION('YEAR', od.createdAt) = :year " +
            "AND od.productEntity.user.id = :vendorId")
    BigDecimal getMonthlyRevenueForVendor(@Param("month") int month,
                                          @Param("vendorId") Long vendorId,
                                          @Param("year") int year);

    @Query("SELECT SUM(od.totalCost) FROM OrderEntity od " +
            "WHERE FUNCTION('MONTH', od.createdAt) = :month " +
            "AND FUNCTION('YEAR', od.createdAt) = :year")
    BigDecimal getMonthlyRevenue(@Param("month") int month,
                                 @Param("year") int year);
}
