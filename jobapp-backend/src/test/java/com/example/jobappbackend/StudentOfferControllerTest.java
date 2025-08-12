package com.example.jobappbackend;

import com.example.jobappbackend.controller.StudentOfferController;
import com.example.jobappbackend.dto.OfferResponse;
import com.example.jobappbackend.exception.GlobalExceptionHandler;
import com.example.jobappbackend.service.OfferService;
import com.example.jobappbackend.service.StudentApplicationService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for {@link StudentOfferController} using standalone {@link MockMvc}.
 * <p>
 * This test class focuses on controller behavior only:
 * dependencies are mocked with Mockito, the Spring context is not started,
 * and a global {@link GlobalExceptionHandler} is registered to map exceptions to HTTP status codes.
 */

@ExtendWith(MockitoExtension.class)
class StudentOfferControllerTest {

    /** Mocked service that provides job offers. */
    @Mock
    private OfferService offerService;

    /** Mocked service that handles student applications (email sending, validation, etc.). */
    @Mock
    private StudentApplicationService applicationService;

    /** Controller under test with mocked dependencies injected. */
    @InjectMocks
    private StudentOfferController studentOfferController;

    /** Standalone MockMvc instance used to perform HTTP requests against the controller. */
    private MockMvc mockMvc;

    /** Simulated authenticated principal used in tests. */
    private Principal mockPrincipal;

    /** Reusable sample offer returned by the mocked service. */
    private OfferResponse sampleOffer;

    /**
     * Initializes Mockito, builds a standalone {@link MockMvc} with the controller
     * and registers the {@link GlobalExceptionHandler} so that exceptions are mapped consistently.
     */
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(studentOfferController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockPrincipal = () -> "student1";

        sampleOffer = new OfferResponse(
                1L,
                "Internship",
                "Great opportunity",
                "http://logo.com/logo.png",
                "http://company.com",
                LocalDateTime.now(),
                "Acme Inc.",
                true
        );
    }

    /**
     * Should return 200 OK and a list of offers for the authenticated student.
     * Verifies JSON fields and that the service is called with the principal name.
     *
     * @throws Exception if the request fails
     */
    @Test
    void shouldGetAllOffers() throws Exception {
        when(offerService.getAllOffers("student1")).thenReturn(List.of(sampleOffer));

        mockMvc.perform(get("/offers")
                        .principal(mockPrincipal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Internship"))
                .andExpect(jsonPath("$[0].applied").value(true));

        verify(offerService).getAllOffers("student1");
    }

    /**
     * Should return 200 OK and the requested offer when it exists.
     *
     * @throws Exception if the request fails
     */
    @Test
    void shouldGetOfferById() throws Exception {
        when(offerService.getOfferById(1L)).thenReturn(sampleOffer);

        mockMvc.perform(get("/offers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Internship"));

        verify(offerService).getOfferById(1L);
    }

    /**
     * Should return 400 Bad Request when the service signals an invalid request for a missing offer.
     * <p>
     * Note: In this project, {@link IllegalArgumentException} is mapped to 400 by {@link GlobalExceptionHandler}.
     * If you later introduce a dedicated NotFoundException mapped to 404, adapt this test accordingly.
     *
     * @throws Exception if the request fails
     */
    @Test
    void shouldReturnBadRequestWhenOfferDoesNotExist() throws Exception {
        when(offerService.getOfferById(99L)).thenThrow(new IllegalArgumentException("Not found"));

        mockMvc.perform(get("/offers/99"))
                .andExpect(status().isBadRequest());

        verify(offerService).getOfferById(99L);
    }

    /**
     * Should return 200 OK when a student applies successfully with CV and motivation files.
     *
     * @throws Exception if the request fails
     */
    @Test
    void shouldApplyToOffer() throws Exception {
        MockMultipartFile cv = new MockMultipartFile("cv", "cv.pdf", "application/pdf", "CV".getBytes());
        MockMultipartFile motivation = new MockMultipartFile("motivation", "motivation.pdf", "application/pdf", "Motivation".getBytes());

        mockPrincipal = () -> "student123";
        doNothing().when(applicationService).applyToOffer(eq(1L), any(), any(), eq("student123"));

        mockMvc.perform(multipart("/offers/1/apply")
                        .file(cv)
                        .file(motivation)
                        .principal(mockPrincipal)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        verify(applicationService).applyToOffer(eq(1L), any(), any(), eq("student123"));
    }

    /**
     * Should return 500 Internal Server Error when email sending fails during application.
     *
     * @throws Exception if the request fails
     */
    @Test
    void shouldReturnServerErrorWhenEmailFails() throws Exception {
        MockMultipartFile cv = new MockMultipartFile("cv", "cv.pdf", "application/pdf", "CV".getBytes());
        MockMultipartFile motivation = new MockMultipartFile("motivation", "motivation.pdf", "application/pdf", "Motivation".getBytes());

        mockPrincipal = () -> "student123";
        doThrow(new MessagingException("SMTP error"))
                .when(applicationService).applyToOffer(eq(1L), any(), any(), eq("student123"));

        mockMvc.perform(multipart("/offers/1/apply")
                        .file(cv)
                        .file(motivation)
                        .principal(mockPrincipal)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isInternalServerError());

        verify(applicationService).applyToOffer(eq(1L), any(), any(), eq("student123"));
    }

    /**
     * Should return 400 Bad Request when the path variable cannot be converted to the expected type.
     * Example: GET /offers/abc where the controller expects a {@code Long id}.
     *
     * @throws Exception if the request fails
     */
    @Test
    void shouldReturnBadRequestForInvalidIdType() throws Exception {
        mockMvc.perform(get("/offers/abc"))
                .andExpect(status().isBadRequest());
    }

    /**
     * Should return 400 Bad Request when applying without required multipart files.
     * <p>
     * Depending on the controller signature, Spring may throw {@code MissingServletRequestPartException}
     * before invoking the service (if {@code @RequestPart(required=true)}), or the service can throw
     * {@link IllegalArgumentException}. Both cases are mapped to 400 by {@link GlobalExceptionHandler}.
     *
     * @throws Exception if the request fails
     */
    @Test
    void shouldReturnBadRequestWhenFilesMissing() throws Exception {
        mockPrincipal = () -> "student123";

        mockMvc.perform(multipart("/offers/1/apply")
                        .principal(mockPrincipal)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    /**
     * Should return 200 OK with an empty JSON array when no offers are available.
     *
     * @throws Exception if the request fails
     */
    @Test
    void shouldReturnEmptyListWhenNoOffersFound() throws Exception {
        when(offerService.getAllOffers("student1")).thenReturn(List.of());

        mockMvc.perform(get("/offers")
                        .principal(mockPrincipal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(offerService).getAllOffers("student1");
    }
}
