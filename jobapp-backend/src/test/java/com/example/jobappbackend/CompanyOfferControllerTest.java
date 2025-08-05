package com.example.jobappbackend;

import com.example.jobappbackend.controller.CompanyOfferController;
import com.example.jobappbackend.dto.OfferRequest;
import com.example.jobappbackend.dto.OfferResponse;
import com.example.jobappbackend.service.OfferService;
import com.example.jobappbackend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class CompanyOfferControllerTest {

    @Mock
    private OfferService offerService;

    @Mock
    private UserService userService;

    @InjectMocks
    private CompanyOfferController companyOfferController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Principal mockPrincipal;

    private OfferResponse sampleOffer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(companyOfferController).build();
        objectMapper = new ObjectMapper();

        mockPrincipal = () -> "rh@acme.com"; // simulate connected user

        sampleOffer = new OfferResponse(
                1L,
                "Junior Java Developer",
                "Great opportunity",
                "https://example.com/logo.png",
                "https://company.com",
                LocalDateTime.now(),
                "ACME Corp",
                false
        );
    }

    @Test
    void shouldGetCompanyOffers() throws Exception {
        when(offerService.getOffersByCompany("rh@acme.com")).thenReturn(List.of(sampleOffer));

        MvcResult result = mockMvc.perform(get("/company/offers")
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertTrue(response.contains("Junior Java Developer"));
        verify(offerService).getOffersByCompany("rh@acme.com");
    }

    @Test
    void shouldCreateOffer() throws Exception {
        OfferRequest request = new OfferRequest(
                "Junior Java Developer",
                "Great opportunity",
                "https://example.com/logo.png",
                "https://company.com"
        );

        when(offerService.createOffer(any(OfferRequest.class), eq("rh@acme.com")))
                .thenReturn(sampleOffer);

        MvcResult result = mockMvc.perform(post("/company/offers")
                        .principal(mockPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertTrue(response.contains("Junior Java Developer"));
        verify(offerService).createOffer(any(OfferRequest.class), eq("rh@acme.com"));
    }

    @Test
    void shouldUpdateOffer() throws Exception {
        OfferRequest request = new OfferRequest(
                "Updated Java Developer",
                "Updated description",
                "https://example.com/logo2.png",
                "https://company.com"
        );

        when(offerService.updateOffer(eq(1L), any(OfferRequest.class), eq("rh@acme.com")))
                .thenReturn(sampleOffer);

        mockMvc.perform(put("/company/offers/1")
                        .principal(mockPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(offerService).updateOffer(eq(1L), any(OfferRequest.class), eq("rh@acme.com"));
    }

    @Test
    void shouldDeleteOffer() throws Exception {
        doNothing().when(offerService).deleteOffer(1L, "rh@acme.com");

        mockMvc.perform(delete("/company/offers/1")
                        .principal(mockPrincipal))
                .andExpect(status().isOk());

        verify(offerService).deleteOffer(1L, "rh@acme.com");
    }
}
