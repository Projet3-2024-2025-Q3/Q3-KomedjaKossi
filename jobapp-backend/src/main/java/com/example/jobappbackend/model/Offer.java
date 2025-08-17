package com.example.jobappbackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a job offer created by a company.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Jobapp_offer")
public class Offer {

    /** Unique identifier for the offer (primary key). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Title of the job offer. */
    private String title;

    /** Detailed description of the job offer. */
    private String description;

    /** URL to the company’s logo image. */
    private String logoUrl;

    /** URL to the company’s website. */
    private String websiteUrl;

    /** Timestamp when the offer was created. */
    private LocalDateTime createdAt = LocalDateTime.now();

    /** The user (company) who created the offer. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User createdBy;

    /**
     * Liste des candidatures pour cette offre.
     * Quand l'offre est supprimée, toutes les candidatures sont supprimées également.
     */
    @OneToMany(mappedBy = "offer", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @ToString.Exclude
    private List<Application> applications = new ArrayList<>();
}
