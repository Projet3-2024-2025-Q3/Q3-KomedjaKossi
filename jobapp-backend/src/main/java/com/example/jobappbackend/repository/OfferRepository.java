package com.example.jobappbackend.repository;

import com.example.jobappbackend.model.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for CRUD operations on {@link Offer}.
 */
@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {

    /**
     * Returns all offers created by a company identified by its username.
     *
     * @param username the username of the company user
     * @return list of offers
     */
    List<Offer> findByCreatedByUsername(String username);

}
