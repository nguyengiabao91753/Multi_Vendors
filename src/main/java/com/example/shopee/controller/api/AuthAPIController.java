package com.example.shopee.controller.api;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.shopee.entity.UserEntity;
import com.example.shopee.payload.dto.LoginRequest;
import com.example.shopee.payload.dto.LoginResponse;
import com.example.shopee.payload.dto.UserDto;
import com.example.shopee.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthAPIController {
	private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public AuthAPIController(UserService userService, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<UserEntity> userOpt = userService.findByEmail(loginRequest.getEmail());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Email not found");
        }

        UserEntity user = userOpt.get();

        if (user.getStatus() == null || user.getStatus() != 1) {
            return ResponseEntity.status(403).body("Account not activated or locked");
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid password");
        }

        UserDto userDto = modelMapper.map(user, UserDto.class);
        return ResponseEntity.ok(new LoginResponse("Login successful", userDto));
    }
}
