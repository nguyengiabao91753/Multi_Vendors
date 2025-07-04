package com.example.shopee.enums;

public enum ReturnStatus {
    PENDING("Chờ xử lý"),
    APPROVED("Đã chấp nhận"),
    REJECTED("Đã từ chối"),
    COMPLETED("Đã hoàn tất");

    private final String displayName;

    ReturnStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

