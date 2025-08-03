package com.example.jobappbackend.service;

import com.example.jobappbackend.dto.ApplicationResponse;
import com.example.jobappbackend.model.Application;
import com.example.jobappbackend.model.Offer;
import com.example.jobappbackend.model.User;
import com.example.jobappbackend.repository.ApplicationRepository;
import com.example.jobappbackend.repository.OfferRepository;
import com.example.jobappbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing job applications.
 */
@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final OfferRepository offerRepository;

    /**
     * Submits an application for a specific student to a specific job offer.
     *
     * @param studentId ID of the student user.
     * @param offerId   ID of the job offer.
     */
    public void apply(Long studentId, Long offerId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new IllegalArgumentException("Offer not found"));

        applicationRepository.findByStudentAndOffer(student, offer).ifPresent(existing -> {
            throw new IllegalStateException("Student already applied to this offer.");
        });

        Application application = new Application();
        application.setStudent(student);
        application.setOffer(offer);
        applicationRepository.save(application);
    }

    /**
     * Retrieves all applications submitted by a student.
     *
     * @param studentId ID of the student.
     * @return List of applications.
     */
    public List<ApplicationResponse> getApplicationsByStudent(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return applicationRepository.findByStudent(student).stream()
                .map(app -> new ApplicationResponse(
                        app.getId(),
                        app.getOffer().getTitle(),
                        app.getOffer().getCreatedBy().getCompanyName(),
                        app.getAppliedAt()
                ))
                .collect(Collectors.toList());
    }

}
