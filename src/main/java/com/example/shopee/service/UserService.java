package com.example.shopee.service;


import com.example.shopee.entity.UserEntity;
import com.example.shopee.payload.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;


public interface UserService extends UserDetailsService {

    Optional<UserEntity> findByEmail(String email);

    boolean validateCredentials(String username, String password);

    UserEntity saveUser(UserEntity user);
}

