package com.example.jobappbackend.repository;

import com.example.jobappbackend.model.Application;
import com.example.jobappbackend.model.Offer;
import com.example.jobappbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Application entities.
 */
@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    /**
     * Finds all applications submitted by a given student.
     */
    List<Application> findByStudent(User student);

    /**
     * Returns the application for a given student and offer, if any.
     */
    Optional<Application> findByStudentAndOffer(User student, Offer offer);

    /**
     * Checks if a student already applied to an offer (by entities).
     */
    boolean existsByStudentAndOffer(User student, Offer offer);

    /**
     * Checks if a student already applied to an offer (by IDs).
     */
    boolean existsByStudent_IdAndOffer_Id(Long studentId, Long offerId);

    /**
     * Lists applications for a given offer (by entity).
     */
    List<Application> findByOffer(Offer offer);

    /**
     * Lists applications for a given offer (by ID).
     */
    List<Application> findByOffer_Id(Long offerId);
}
