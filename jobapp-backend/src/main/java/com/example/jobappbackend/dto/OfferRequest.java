package com.example.jobappbackend.dto;
import lombok.*;

/**
 * Dto for creating or updating an offer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfferRequest {
    private String title;
    private String description;
    private String logoUrl;
    private String websiteUrl;
}
