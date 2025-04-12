package com.example.shopee.service.impl;


import com.example.shopee.entity.RoleEntity;
import com.example.shopee.repository.RoleRepository;
import com.example.shopee.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    @Override
    public Optional<RoleEntity> findById(Long id) {
        return roleRepository.findById(id);
    }
}
