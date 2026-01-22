package com.merko.merko_backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShippingInfo {
    private String firstName;
    private String lastName;
    private String companyName;
    private String address;
    private String apartment;
    private String city;
    private String state;
    private String zipCode;
    private String phoneNumber;
}
