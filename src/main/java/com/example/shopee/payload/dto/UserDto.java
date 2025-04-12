package com.example.shopee.payload.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDto {
    private Long id;
    private String fullName;
    private String email;
    private String dob;
    private String gender;
    private String address;
    private String phone;
    private String password;
    private String newPassword;
    private String imageLink;
    private Long imageId;
    private Long blogCount;
    private Long feedbackCount;
    private Integer status;
    private LocalDateTime createdAt;
    private String createdDate;

    public UserDto(){}

    public UserDto(Long id, String email, Long blogCount, Long feedbackCount, Integer status,LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.blogCount = blogCount;
        this.feedbackCount = feedbackCount;
        this.status = status;
        this.createdAt = createdAt;
    }
}
