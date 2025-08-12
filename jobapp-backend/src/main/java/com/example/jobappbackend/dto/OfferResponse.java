package com.example.jobappbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO exposing job offer data to the frontend.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfferResponse {

    /** Offer identifier. */
    private Long id;

    /** Offer title. */
    private String title;

    /** Offer description. */
    private String description;

    /** Company logo URL (optional). */
    private String logoUrl;

    /** Company website URL (optional). */
    private String websiteUrl;

    /** Creation timestamp. */
    private LocalDateTime createdAt;

    /** Company name (owner). */
    private String companyName;

    /** Whether the current student has applied to this offer. */
    private boolean applied;
}
