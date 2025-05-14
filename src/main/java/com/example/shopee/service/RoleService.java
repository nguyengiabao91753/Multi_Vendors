package com.example.shopee.service;

import com.example.shopee.entity.RoleEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public interface RoleService {
    Optional<RoleEntity> findById(Long id);
}

