package com.example.jobappbackend.controller;

import com.example.jobappbackend.dto.ApplicationResponse;
import com.example.jobappbackend.model.Application;
import com.example.jobappbackend.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing job application-related endpoints.
 */
@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ApplicationController {

    private final ApplicationService applicationService;

    /**
     * Endpoint to retrieve all applications submitted by a specific student.
     *
     * @param studentId ID of the student user.
     * @return List of applications.
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsByStudent(@PathVariable Long studentId) {
        List<ApplicationResponse> applications = applicationService.getApplicationsByStudent(studentId);
        return ResponseEntity.ok(applications);
    }
}
