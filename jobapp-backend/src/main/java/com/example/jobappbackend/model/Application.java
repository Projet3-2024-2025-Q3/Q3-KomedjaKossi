package com.example.jobappbackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
@Getter
@Setter
public class Application {
    @Id
    @GeneratedValue
    private Long id;

    /** Étudiant qui a postulé (PAS de cascade vers User). */
    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id") // FK -> Jobapp_user(id)
    private User student;

    /** Offre concernée (PAS de cascade vers Offer). */
    @ManyToOne(optional = false)
    @JoinColumn(name = "offre_id") // FK -> Jobapp_offer(id)
    private Offer offer;

    private LocalDateTime appliedAt;
}
