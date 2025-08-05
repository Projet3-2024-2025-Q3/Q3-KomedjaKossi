package com.example.jobappbackend;

import com.example.jobappbackend.controller.StudentOfferController;
import com.example.jobappbackend.dto.OfferResponse;
import com.example.jobappbackend.service.OfferService;
import com.example.jobappbackend.service.StudentApplicationService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for StudentOfferController.
 */
@SpringBootTest
class StudentOfferControllerTest {

    @Mock
    private OfferService offerService;

    @Mock
    private StudentApplicationService applicationService;

    @InjectMocks
    private StudentOfferController studentOfferController;

    private MockMvc mockMvc;
    private Principal mockPrincipal;
    private OfferResponse sampleOffer;

    /**
     * Setup test environment before each test.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(studentOfferController).build();

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
     * Test to retrieve all job offers with applied status.
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
     * Test to retrieve a specific offer by ID.
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
     * Test to apply to an offer with CV and motivation letter.
     */
    @Test
    void shouldApplyToOffer() throws Exception {
        MockMultipartFile cv = new MockMultipartFile("cv", "cv.pdf", "application/pdf", "Dummy CV".getBytes());
        MockMultipartFile motivation = new MockMultipartFile("motivation", "motivation.pdf", "application/pdf", "Dummy Motivation".getBytes());

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
}
