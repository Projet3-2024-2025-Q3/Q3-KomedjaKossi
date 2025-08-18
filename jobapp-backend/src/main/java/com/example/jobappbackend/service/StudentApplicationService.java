package com.example.jobappbackend.service;

import com.example.jobappbackend.exception.ApiException;
import com.example.jobappbackend.model.Application;
import com.example.jobappbackend.model.Offer;
import com.example.jobappbackend.model.User;
import com.example.jobappbackend.repository.ApplicationRepository;
import com.example.jobappbackend.repository.OfferRepository;
import com.example.jobappbackend.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

/**
 * Service for handling student job applications: sends email and saves to database.
 */
@Service
public class StudentApplicationService {

    private final OfferRepository offerRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final JavaMailSender mailSender;

    @Autowired
    public StudentApplicationService(OfferRepository offerRepository,
                                     UserRepository userRepository,
                                     ApplicationRepository applicationRepository,
                                     JavaMailSender mailSender) {
        this.offerRepository = offerRepository;
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
        this.mailSender = mailSender;
    }

    /**
     * Sends an application email with attachments (CV + motivation letter) to the company,
     * and saves the application in the database.
     *
     * @param offerId         the ID of the offer
     * @param cv              the CV file (required)
     * @param motivation      the motivation letter file (required)
     * @param studentUsername the username of the student (from Principal)
     * @throws MessagingException if email sending fails
     */
    @Transactional(rollbackFor = MessagingException.class)
    public void applyToOffer(Long offerId, MultipartFile cv, MultipartFile motivation, String studentUsername) throws MessagingException {
        if (cv == null || cv.isEmpty() || motivation == null || motivation.isEmpty()) {
            throw new ApiException("CV and motivation letter are required.");
        }

        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new ApiException("Offer not found"));

        User student = userRepository.findByUsername(studentUsername)
                .orElseThrow(() -> new ApiException("Student not found"));

        boolean alreadyApplied = applicationRepository.existsByStudent_IdAndOffer_Id(student.getId(), offer.getId());
        if (alreadyApplied) {
            throw new ApiException("You have already applied to this offer.");
        }

        Application application = new Application();
        application.setStudent(student);
        application.setOffer(offer);
        application.setAppliedAt(LocalDateTime.now());
        applicationRepository.save(application);

        User company = offer.getCreatedBy();
        if (company == null || company.getEmail() == null) {
            throw new ApiException("Company email not available.");
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(company.getEmail());
        helper.setSubject("New Job Application for: " + offer.getTitle());
        helper.setText("Dear " + (company.getCompanyName() != null ? company.getCompanyName() : "Company") + ",\n\n"
                + "A student has applied to your offer \"" + offer.getTitle() + "\".\n"
                + "Email: " + student.getEmail() + "\n\n"
                + "Please find the attached CV and motivation letter.\n\nRegards,\nJobApp");

        addAttachment(helper, cv);
        addAttachment(helper, motivation);

        mailSender.send(message);
    }

    /**
     * Adds a file as an attachment to the email.
     *
     * @param helper the email message helper
     * @param file   the file to attach
     * @throws MessagingException if adding the attachment fails
     */
    private void addAttachment(MimeMessageHelper helper, MultipartFile file) throws MessagingException {
        if (file == null || file.isEmpty()) return;
        String filename = (file.getOriginalFilename() != null && !file.getOriginalFilename().isBlank())
                ? file.getOriginalFilename()
                : "attachment";
        InputStreamSource source = file::getInputStream;
        helper.addAttachment(filename, source);
    }
}
