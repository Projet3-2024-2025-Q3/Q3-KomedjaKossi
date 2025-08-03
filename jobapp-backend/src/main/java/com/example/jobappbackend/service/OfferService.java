package com.example.jobappbackend.service;

import com.example.jobappbackend.dto.OfferRequest;
import com.example.jobappbackend.dto.OfferResponse;
import com.example.jobappbackend.model.Offer;
import com.example.jobappbackend.model.User;
import com.example.jobappbackend.repository.OfferRepository;
import com.example.jobappbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing job offers created by companies.
 */
@Service
public class OfferService {

    private final OfferRepository offerRepository;
    private final UserRepository userRepository;

    @Autowired
    public OfferService(OfferRepository offerRepository, UserRepository userRepository) {
        this.offerRepository = offerRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new job offer for a given company (user).
     *
     * @param request         the offer data
     * @param companyUsername the username of the company creating the offer
     * @return the created offer as a response DTO
     */
    public OfferResponse createOffer(OfferRequest request, String companyUsername) {

        User company = userRepository.findByUsername(companyUsername)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        Offer offer = new Offer();
        offer.setTitle(request.getTitle());
        offer.setDescription(request.getDescription());
        offer.setLogoUrl(request.getLogoUrl());
        offer.setWebsiteUrl(request.getWebsiteUrl());
        offer.setCreatedAt(LocalDateTime.now());
        offer.setCreatedBy(company);

        return toDto(offerRepository.save(offer));
    }

    /**
     * Retrieves all job offers created by a specific company.
     *
     * @param companyUsername the username of the company
     * @return a list of offers
     */
    public List<OfferResponse> getOffersByCompany(String companyUsername) {
        return offerRepository.findByCreatedByUsername(companyUsername).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing job offer.
     *
     * @param id              the ID of the offer to update
     * @param request         the updated offer data
     * @param companyUsername the username of the company requesting the update
     * @return the updated offer
     */
    public OfferResponse updateOffer(Long id, OfferRequest request, String companyUsername) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offer not found"));

        if (!offer.getCreatedBy().getUsername().equals(companyUsername)) {
            throw new RuntimeException("You are not authorized to update this offer.");
        }

        offer.setTitle(request.getTitle());
        offer.setDescription(request.getDescription());
        offer.setLogoUrl(request.getLogoUrl());
        offer.setWebsiteUrl(request.getWebsiteUrl());

        Offer updatedOffer = offerRepository.save(offer);
        return toDto(updatedOffer);
    }

    /**
     * Deletes an offer by ID if it belongs to the specified company.
     *
     * @param id              the offer ID
     * @param companyUsername the username of the company performing the deletion
     */
    public void deleteOffer(Long id, String companyUsername) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offer not found"));

        if (!offer.getCreatedBy().getUsername().equals(companyUsername)) {
            throw new RuntimeException("You are not authorized to delete this offer.");
        }

        offerRepository.deleteById(id);
    }

    /**
     * Converts an Offer entity to OfferResponse DTO.
     *
     * @param offer the offer entity
     * @return the corresponding response DTO
     */
    private OfferResponse toDto(Offer offer) {
        return new OfferResponse(
                offer.getId(),
                offer.getTitle(),
                offer.getDescription(),
                offer.getLogoUrl(),
                offer.getWebsiteUrl(),
                offer.getCreatedAt(),
                offer.getCreatedBy().getCompanyName() // or getUsername() if preferred
        );
    }
}
