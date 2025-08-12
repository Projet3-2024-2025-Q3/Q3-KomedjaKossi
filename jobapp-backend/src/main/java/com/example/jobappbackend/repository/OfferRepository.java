package com.example.jobappbackend.repository;

import com.example.jobappbackend.model.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing {@link Offer} entities.
 * <p>
 * Provides CRUD operations and query methods for retrieving
 * offers based on the company that created them.
 */
@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {

    /**
     * Finds all offers created by a company using the company's username.
     *
     * @param username the username of the company user.
     * @return a list of matching offers.
     */
    List<Offer> findByCreatedByUsername(String username);

    /**
     * Finds all offers created by a company using the company's user ID.
     *
     * @param userId the ID of the company user.
     * @return a list of matching offers.
     */
    List<Offer> findByCreatedBy_Id(Long userId);
}
