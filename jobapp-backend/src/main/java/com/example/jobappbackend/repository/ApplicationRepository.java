package com.example.jobappbackend.repository;

import com.example.jobappbackend.model.Application;
import com.example.jobappbackend.model.Offer;
import com.example.jobappbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Application entities.
 */
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    /**
     * Finds all applications submitted by a given student.
     *
     * @param student The student user.
     * @return List of applications.
     */
    List<Application> findByStudent(User student);

    /**
     * Checks if a student has already applied to a given offer.
     *
     * @param student The student.
     * @param offer   The offer.
     * @return Optional application.
     */
    Optional<Application> findByStudentAndOffer(User student, Offer offer);
}
