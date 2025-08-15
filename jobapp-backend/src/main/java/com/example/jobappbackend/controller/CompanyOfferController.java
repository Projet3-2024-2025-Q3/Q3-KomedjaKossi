package com.example.jobappbackend.controller;

import com.example.jobappbackend.dto.OfferRequest;
import com.example.jobappbackend.dto.OfferResponse;
import com.example.jobappbackend.service.OfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller allowing a company user to manage its own job offers.
 * <p>
 * Endpoints are exposed under /company/offers and secured by SecurityConfig.
 */
@RestController
@RequestMapping("/company/offers")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CompanyOfferController {

    /** Business service handling job offer operations. */
    private final OfferService offerService;

    /**
     * Creates a new job offer for the authenticated company.
     *
     * @param request the {@link OfferRequest} payload containing offer details
     * @param userId  the ID of the company creating the offer
     * @return the created {@link OfferResponse}
     */
    @PostMapping
    public OfferResponse createOffer(@RequestBody final OfferRequest request,
                                     @RequestParam final Long userId) {
        return offerService.createOffer(request, userId);
    }

    /**
     * Retrieves all job offers created by the authenticated company.
     *
     * @param userId the ID of the company whose offers to retrieve
     * @return a list of {@link OfferResponse} belonging to the company
     */
    @GetMapping
    public List<OfferResponse> getCompanyOffers(@RequestParam final Long userId) {
        return offerService.getOffersByCompany(userId);
    }

    /**
     * Updates an existing job offer owned by the authenticated company.
     *
     * @param id      the identifier of the offer to update
     * @param request the {@link OfferRequest} payload with updated data
     * @param userId  the ID of the company updating the offer
     * @return the updated {@link OfferResponse}
     */
    @PutMapping("/{id}")
    public OfferResponse updateOffer(@PathVariable final Long id,
                                     @RequestBody final OfferRequest request,
                                     @RequestParam final Long userId) {
        return offerService.updateOffer(id, request, userId);
    }

    /**
     * Deletes a job offer owned by the authenticated company.
     *
     * @param id     the identifier of the offer to delete
     * @param userId the ID of the company deleting the offer
     */
    @DeleteMapping("/{id}")
    public void deleteOffer(@PathVariable final Long id,
                            @RequestParam final Long userId) {
        offerService.deleteOffer(id, userId);
    }
}