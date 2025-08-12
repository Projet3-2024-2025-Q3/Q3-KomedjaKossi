package com.example.jobappbackend.controller;

import com.example.jobappbackend.dto.ApplicationResponse;
import com.example.jobappbackend.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for student application queries.
 * Routes are under /applications/student to match SecurityConfig.
 */
@RestController
@RequestMapping("/applications/student")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ApplicationController {

    private final ApplicationService applicationService;

    /**
     * Retrieves all applications submitted by a specific student.
     *
     * @param studentId ID of the student user.
     * @return list of applications for the student.
     */
    @GetMapping("/{studentId}")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsByStudent(@PathVariable Long studentId) {
        List<ApplicationResponse> applications = applicationService.getApplicationsByStudent(studentId);
        return ResponseEntity.ok(applications);
        // Option (plus tard) : récupérer l'ID depuis l'utilisateur connecté (Principal) plutôt qu'en path.
    }
}
