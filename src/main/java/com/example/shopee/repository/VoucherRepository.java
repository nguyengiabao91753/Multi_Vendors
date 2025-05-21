package com.example.shopee.repository;

import com.example.shopee.entity.CategoryEntity;
import com.example.shopee.entity.VoucherEntity;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@SpringBootApplication
public interface VoucherRepository extends JpaRepository<VoucherEntity, Long> {
    List<VoucherEntity> findAllByUserEntity_Id(Long userId);

    Optional<VoucherEntity> findByName(String name);


}
