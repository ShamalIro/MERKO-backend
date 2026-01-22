package com.merko.merko_backend.dto;

public class CreateInquiryDTO {
    private String userType;
    private String topic;
    private String subTopic;
    private String description;

    // Constructors
    public CreateInquiryDTO() {}

    public CreateInquiryDTO(String userType, String topic, String subTopic, String description) {
        this.userType = userType;
        this.topic = topic;
        this.subTopic = subTopic;
        this.description = description;
    }

    // Getters and Setters
    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public String getSubTopic() { return subTopic; }
    public void setSubTopic(String subTopic) { this.subTopic = subTopic; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}