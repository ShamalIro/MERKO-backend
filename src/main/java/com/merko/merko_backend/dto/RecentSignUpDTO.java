package com.merko.merko_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecentSignUpDTO {
    private Long id;
    private String name;
    private String company;
    private String role;
    private LocalDateTime date;
    private String status;
    private String email;
}