package com.example.shopee.repository;

import com.example.shopee.entity.RoleEntity;
import com.example.shopee.enums.RoleEnum;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.management.relation.Role;
import java.util.Optional;

@Repository
@SpringBootApplication
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findById(Long id);

    RoleEntity findByName(RoleEnum roleEnum);
}
