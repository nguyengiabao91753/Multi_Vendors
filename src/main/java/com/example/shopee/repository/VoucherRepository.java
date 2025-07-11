package com.example.shopee.repository;

import com.example.shopee.entity.CategoryEntity;
import com.example.shopee.entity.VoucherEntity;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@SpringBootApplication
public interface VoucherRepository extends JpaRepository<VoucherEntity, Long> {
    List<VoucherEntity> findAllByUserEntity_Id(Long userId);
    Optional<VoucherEntity> findByName(String name);

    @Query("SELECT v FROM VoucherEntity v WHERE v.userEntity.id = :sellerId")
    List<VoucherEntity> findAllBySellerId(@Param("sellerId") Long sellerId);

    @Query("SELECT COUNT(v) FROM VoucherEntity v WHERE v.userEntity.id = :userId")
    int countByUserId(@Param("userId") Long userId);

}
