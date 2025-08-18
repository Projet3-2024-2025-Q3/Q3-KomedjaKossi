package com.example.jobappbackend.controller;

import com.example.jobappbackend.dto.OfferRequest;
import com.example.jobappbackend.dto.OfferResponse;
import com.example.jobappbackend.exception.ApiException;
import com.example.jobappbackend.service.OfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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
     * @param jwt     authentication principal providing the 'userId' claim
     * @param userId  optional company user ID (fallback if provided explicitly)
     * @return the created {@link OfferResponse}
     */
    @PostMapping
    public OfferResponse createOffer(@RequestBody final OfferRequest request,
                                     @AuthenticationPrincipal Jwt jwt,
                                     @RequestParam(name = "userId", required = false) final Long userId) {
        Long companyUserId = resolveCompanyUserId(jwt, userId);
        return offerService.createOffer(request, companyUserId);
    }

    /**
     * Retrieves all job offers created by the authenticated company.
     *
     * @param jwt    authentication principal providing the 'userId' claim
     * @param userId optional company user ID (fallback if provided explicitly)
     * @return a list of {@link OfferResponse} belonging to the company
     */
    @GetMapping
    public List<OfferResponse> getCompanyOffers(@AuthenticationPrincipal Jwt jwt,
                                                @RequestParam(name = "userId", required = false) final Long userId) {
        Long companyUserId = resolveCompanyUserId(jwt, userId);
        return offerService.getOffersByCompany(companyUserId);
    }

    /**
     * Updates an existing job offer owned by the authenticated company.
     *
     * @param id      the identifier of the offer to update
     * @param request the {@link OfferRequest} payload with updated data
     * @param jwt     authentication principal providing the 'userId' claim
     * @param userId  optional company user ID (fallback if provided explicitly)
     * @return the updated {@link OfferResponse}
     */
    @PutMapping("/{id}")
    public OfferResponse updateOffer(@PathVariable final Long id,
                                     @RequestBody final OfferRequest request,
                                     @AuthenticationPrincipal Jwt jwt,
                                     @RequestParam(name = "userId", required = false) final Long userId) {
        Long companyUserId = resolveCompanyUserId(jwt, userId);
        return offerService.updateOffer(id, request, companyUserId);
    }

    /**
     * Deletes a job offer owned by the authenticated company.
     *
     * @param id     the identifier of the offer to delete
     * @param jwt    authentication principal providing the 'userId' claim
     * @param userId optional company user ID (fallback if provided explicitly)
     */
    @DeleteMapping("/{id}")
    public void deleteOffer(@PathVariable final Long id,
                            @AuthenticationPrincipal Jwt jwt,
                            @RequestParam(name = "userId", required = false) final Long userId) {
        Long companyUserId = resolveCompanyUserId(jwt, userId);
        offerService.deleteOffer(id, companyUserId);
    }

    /** Resolve company user ID from JWT claim 'userId', else from request param; otherwise fail. */
    private Long resolveCompanyUserId(Jwt jwt, Long userId) {
        if (jwt != null) {
            Long fromJwt = jwt.getClaim("userId");
            if (fromJwt != null) return fromJwt;
            if (userId == null) {
                throw new ApiException("Missing 'userId' claim in token");
            }
        }
        if (userId != null) return userId;
        throw new ApiException("User ID not provided");
    }
}
