package com.example.jobappbackend.dto;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO for exposing offer data to the front end.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfferResponse {
    private Long id;
    private String title;
    private String description;
    private String logoUrl;
    private String websiteUrl;
    private LocalDateTime createdAt;
    private String companyName;
    private boolean applied;
}