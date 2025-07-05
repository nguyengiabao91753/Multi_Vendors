package com.example.shopee.enums;

public enum ReturnStatus {
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Cancelled"),
    COMPLETED("Completed");

    private final String displayName;

    ReturnStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

