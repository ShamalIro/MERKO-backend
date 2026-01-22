package com.merko.merko_backend.dto;

public class StatusUpdateDTO {
    private String status;

    // Constructors
    public StatusUpdateDTO() {}

    public StatusUpdateDTO(String status) {
        this.status = status;
    }

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}