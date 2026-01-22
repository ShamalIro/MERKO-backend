package com.merko.merko_backend.dto;

public class UpdateInquiryDTO {
    private String topic;
    private String subTopic;
    private String description;
    private String status; // ADD THIS FIELD

    // Constructors
    public UpdateInquiryDTO() {}

    public UpdateInquiryDTO(String topic, String subTopic, String description, String status) {
        this.topic = topic;
        this.subTopic = subTopic;
        this.description = description;
        this.status = status;
    }

    // Getters and Setters
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public String getSubTopic() { return subTopic; }
    public void setSubTopic(String subTopic) { this.subTopic = subTopic; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; } // ADD GETTER
    public void setStatus(String status) { this.status = status; } // ADD SETTER
}