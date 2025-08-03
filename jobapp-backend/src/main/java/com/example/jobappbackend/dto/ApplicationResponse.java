package com.example.jobappbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO used to return a student's application in a safe and readable format.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationResponse {

    private Long id;
    private String offerTitle;
    private String companyName;
    private LocalDateTime appliedAt;


}
