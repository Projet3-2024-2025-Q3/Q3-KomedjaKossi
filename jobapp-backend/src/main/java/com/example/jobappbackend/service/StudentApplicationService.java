package com.example.jobappbackend.service;

import com.example.jobappbackend.model.Offer;
import com.example.jobappbackend.model.User;
import com.example.jobappbackend.repository.OfferRepository;
import com.example.jobappbackend.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class StudentApplicationService {

    private final OfferRepository offerRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    @Autowired
    public StudentApplicationService(OfferRepository offerRepository,
                                     UserRepository userRepository,
                                     JavaMailSender mailSender) {
        this.offerRepository = offerRepository;
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    /**
     * Sends an application email with attachments (CV + motivation letter) to the company.
     *
     * @param offerId    the ID of the offer
     * @param cv         the CV file
     * @param motivation the motivation letter file
     * @param studentEmail the email of the student (authenticated principal)
     */
    public void applyToOffer(Long offerId, MultipartFile cv, MultipartFile motivation, String studentEmail) throws MessagingException {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found"));

        User company = offer.getCreatedBy();
        if (company == null || company.getEmail() == null) {
            throw new RuntimeException("Company email not available.");
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(company.getEmail());
        helper.setSubject("New Job Application for: " + offer.getTitle());
        helper.setText("Dear " + company.getCompanyName() + ",\n\n"
                + "A student has applied to your offer \"" + offer.getTitle() + "\".\n"
                + "Email: " + studentEmail + "\n\n"
                + "Please find the attached CV and motivation letter.\n\nRegards,\nJobApp");

        addAttachment(helper, cv);
        addAttachment(helper, motivation);

        mailSender.send(message);
    }

    private void addAttachment(MimeMessageHelper helper, MultipartFile file) throws MessagingException {
            String filename = file.getOriginalFilename();
            InputStreamSource source = file::getInputStream;
            helper.addAttachment(filename, source);

    }
}
