package com.example.jobappbackend.model;

import com.example.jobappbackend.model.Offer;
import com.example.jobappbackend.model.User;
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

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne
    @JoinColumn(name = "offre_id")
    private Offer offer;

    private LocalDateTime appliedAt;


}
