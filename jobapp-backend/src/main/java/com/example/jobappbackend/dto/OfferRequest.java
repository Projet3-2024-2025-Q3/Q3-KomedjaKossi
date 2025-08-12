package com.example.jobappbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating or updating a job offer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfferRequest {

    /** Offer title. */
    private String title;

    /** Offer description. */
    private String description;

    /** Company logo URL (optional). */
    private String logoUrl;

    /** Company website URL (optional). */
    private String websiteUrl;
}
