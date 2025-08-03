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

@SpringBootTest
class StudentOfferControllerTest {

    @Mock
    private OfferService offerService;

    @Mock
    private StudentApplicationService applicationService;

    @InjectMocks
    private StudentOfferController studentOfferController;

    private MockMvc mockMvc;

    private OfferResponse sampleOffer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(studentOfferController).build();

        sampleOffer = new OfferResponse(
                1L, "Internship", "Great opportunity",
                "http://logo.com/logo.png", "http://company.com",
                LocalDateTime.now(), "Acme Inc."
        );
    }

    @Test
    void shouldGetAllOffers() throws Exception {
        when(offerService.getAllOffers()).thenReturn(List.of(sampleOffer));

        mockMvc.perform(get("/offers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Internship"));

        verify(offerService).getAllOffers();
    }

    @Test
    void shouldGetOfferById() throws Exception {
        when(offerService.getOfferById(1L)).thenReturn(sampleOffer);

        mockMvc.perform(get("/offers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Internship"));

        verify(offerService).getOfferById(1L);
    }

    @Test
    void shouldApplyToOffer() throws Exception {
        MockMultipartFile cv = new MockMultipartFile("cv", "cv.pdf", "application/pdf", "Dummy CV content".getBytes());
        MockMultipartFile motivation = new MockMultipartFile("motivation", "motivation.pdf", "application/pdf", "Dummy Motivation content".getBytes());

        Principal principal = () -> "student123";

        doNothing().when(applicationService).applyToOffer(eq(1L), any(), any(), eq("student123"));

        mockMvc.perform(multipart("/offers/1/apply")
                        .file(cv)
                        .file(motivation)
                        .principal(principal)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        verify(applicationService).applyToOffer(eq(1L), any(), any(), eq("student123"));
    }
}
