package com.example.jobappbackend.controller;

import com.example.jobappbackend.dto.OfferRequest;
import com.example.jobappbackend.dto.OfferResponse;
import com.example.jobappbackend.service.OfferService;
import com.example.jobappbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * Controller for companies to manage their own job offers.
 */
@RestController
@RequestMapping("/company/offers")
@PreAuthorize("hasRole('COMPANY')")
@CrossOrigin(origins = "*")
public class CompanyOfferController {

    @Autowired
    private OfferService offerService;

    @Autowired
    private UserService userService;

    @PostMapping
    public OfferResponse createOffer(@RequestBody OfferRequest request, Principal principal) {
        return offerService.createOffer(request, principal.getName());
    }

    @GetMapping
    public List<OfferResponse> getCompanyOffers(Principal principal) {
        return offerService.getOffersByCompany(principal.getName());
    }

    @PutMapping("/{id}")
    public OfferResponse updateOffer(@PathVariable Long id,
                                     @RequestBody OfferRequest request,
                                     Principal principal) {
        return offerService.updateOffer(id, request, principal.getName());
    }

    @DeleteMapping("/{id}")
    public void deleteOffer(@PathVariable Long id, Principal principal) {
        offerService.deleteOffer(id, principal.getName());
    }
}
