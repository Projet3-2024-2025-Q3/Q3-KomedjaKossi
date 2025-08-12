package com.example.jobappbackend.controller;

import com.example.jobappbackend.dto.OfferResponse;
import com.example.jobappbackend.service.OfferService;
import com.example.jobappbackend.service.StudentApplicationService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

/**
 * Controller for students to view and apply to job offers.
 * Routes are under /student/offers to match SecurityConfig.
 */
@RestController
@RequestMapping("/offers")
@CrossOrigin(origins = "*")
public class StudentOfferController {

    @Autowired
    private OfferService offerService;

    @Autowired
    private StudentApplicationService applicationService;

    /**
     * Get all available job offers for the authenticated student.
     *
     * @param principal the authenticated user
     * @return list of offers with "applied" flag
     */
    @GetMapping
    public List<OfferResponse> getAllOffers(Principal principal) {
        return offerService.getAllOffers(principal.getName());
    }

    /**
     * Get details of a specific job offer.
     *
     * @param id offer identifier
     * @return offer details
     */
    @GetMapping("/{id}")
    public OfferResponse getOfferById(@PathVariable Long id) {
        return offerService.getOfferById(id);
    }

    /**
     * Apply to a job offer with CV and motivation letter.
     *
     * @param id          offer identifier
     * @param cv          CV file (multipart)
     * @param motivation  motivation letter file (multipart)
     * @param principal   the authenticated student
     * @throws MessagingException if email sending fails
     */
    @PostMapping(value = "/{id}/apply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void applyToOffer(@PathVariable Long id,
                             @RequestParam("cv") MultipartFile cv,
                             @RequestParam("motivation") MultipartFile motivation,
                             Principal principal) throws MessagingException {
        applicationService.applyToOffer(id, cv, motivation, principal.getName());
    }
}
