package com.merko.merko_backend.entity;

public enum InquiryStatus {
    PENDING("Pending"),
    PROCESSING("Processing"),
    HOLD("Hold"),
    RESPONDED("Responded"),
    CLOSED("Closed");

    private final String displayName;

    InquiryStatus(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return this.displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}