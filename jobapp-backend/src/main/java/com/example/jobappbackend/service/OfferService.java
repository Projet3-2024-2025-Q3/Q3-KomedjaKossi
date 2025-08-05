package com.example.jobappbackend.service;

import com.example.jobappbackend.dto.OfferRequest;
import com.example.jobappbackend.dto.OfferResponse;
import com.example.jobappbackend.exception.ApiException;
import com.example.jobappbackend.model.Offer;
import com.example.jobappbackend.model.User;
import com.example.jobappbackend.repository.ApplicationRepository;
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
    private final ApplicationRepository applicationRepository;

    @Autowired
    public OfferService(OfferRepository offerRepository,
                        UserRepository userRepository,
                        ApplicationRepository applicationRepository) {
        this.offerRepository = offerRepository;
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
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
                .orElseThrow(() -> new ApiException("Company not found"));

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
                .orElseThrow(() -> new ApiException("Offer not found"));

        if (!offer.getCreatedBy().getUsername().equals(companyUsername)) {
            throw new ApiException("You are not authorized to update this offer.");
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
                .orElseThrow(() -> new ApiException("Offer not found"));

        if (!offer.getCreatedBy().getUsername().equals(companyUsername)) {
            throw new ApiException("You are not authorized to delete this offer.");
        }

        offerRepository.deleteById(id);
    }

    /**
     * Retrieves a single job offer by its ID.
     *
     * @param id the offer ID
     * @return the corresponding OfferResponse DTO
     */
    public OfferResponse getOfferById(Long id) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new ApiException("Offer not found"));
        return toDto(offer);
    }

    /**
     * Retrieves all job offers and adds 'applied' info for the connected student.
     *
     * @param studentUsername the username of the student
     * @return list of offers with applied status
     */
    public List<OfferResponse> getAllOffers(String studentUsername) {
        User student = userRepository.findByUsername(studentUsername)
                .orElseThrow(() -> new ApiException("Student not found"));

        return offerRepository.findAll().stream()
                .map(offer -> {
                    boolean applied = applicationRepository
                            .findByStudentAndOffer(student, offer)
                            .isPresent();
                    return toDto(offer, applied);
                })
                .collect(Collectors.toList());
    }

    /**
     * Converts an Offer entity to OfferResponse DTO without applied status.
     *
     * @param offer the offer entity
     * @return DTO with default applied = false
     */
    private OfferResponse toDto(Offer offer) {
        return new OfferResponse(
                offer.getId(),
                offer.getTitle(),
                offer.getDescription(),
                offer.getLogoUrl(),
                offer.getWebsiteUrl(),
                offer.getCreatedAt(),
                offer.getCreatedBy().getCompanyName(),
                false
        );
    }

    /**
     * Converts an Offer entity to OfferResponse DTO with applied status.
     *
     * @param offer   the offer entity
     * @param applied whether the student has applied
     * @return the full DTO
     */
    private OfferResponse toDto(Offer offer, boolean applied) {
        return new OfferResponse(
                offer.getId(),
                offer.getTitle(),
                offer.getDescription(),
                offer.getLogoUrl(),
                offer.getWebsiteUrl(),
                offer.getCreatedAt(),
                offer.getCreatedBy().getCompanyName(),
                applied
        );
    }
}
