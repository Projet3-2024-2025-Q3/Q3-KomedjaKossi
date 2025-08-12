package com.example.jobappbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO representing a student's job application in a safe, readable format.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationResponse {

    /** Application identifier. */
    private Long id;

    /** Title of the related offer. */
    private String offerTitle;

    /** Company name of the offer owner. */
    private String companyName;

    /** Submission timestamp. */
    private LocalDateTime appliedAt;
}
