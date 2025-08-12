package com.example.jobappbackend;

import com.example.jobappbackend.controller.CompanyOfferController;
import com.example.jobappbackend.dto.OfferRequest;
import com.example.jobappbackend.dto.OfferResponse;
import com.example.jobappbackend.exception.GlobalExceptionHandler;
import com.example.jobappbackend.service.OfferService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for {@link CompanyOfferController} using standalone {@link MockMvc}.
 * <p>
 * The service layer is mocked. A {@link GlobalExceptionHandler} is registered to map exceptions to HTTP codes.
 */
@ExtendWith(MockitoExtension.class)
class CompanyOfferControllerTest {

    /** Mocked business service handling offers. */
    @Mock
    private OfferService offerService;

    /** Controller under test with mocked dependencies injected by Mockito. */
    @InjectMocks
    private CompanyOfferController companyOfferController;

    /** Standalone MockMvc client to perform HTTP requests against the controller. */
    private MockMvc mockMvc;

    /** JSON serializer/deserializer for request/response payloads. */
    private ObjectMapper objectMapper;

    /** Simulated authenticated principal representing the company user. */
    private Principal mockPrincipal;

    /** Reusable sample offer returned by the mocked service. */
    private OfferResponse sampleOffer;

    /**
     * Initializes {@link MockMvc}, registers the global exception handler, and prepares common fixtures.
     */
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(companyOfferController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        mockPrincipal = () -> "rh@acme.com";

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

    /**
     * Should return 200 OK and the list of offers for the authenticated company.
     *
     * @throws Exception if the HTTP call fails
     */
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
        verifyNoMoreInteractions(offerService);
    }

    /**
     * Should return 200 OK and the created offer when creation succeeds.
     *
     * @throws Exception if the HTTP call fails
     */
    @Test
    void shouldCreateOffer() throws Exception {
        OfferRequest request = new OfferRequest(
                "Junior Java Developer",
                "Great opportunity",
                "https://example.com/logo.png",
                "https://company.com"
        );

        when(offerService.createOffer(any(OfferRequest.class), eq("rh@acme.com"))).thenReturn(sampleOffer);

        MvcResult result = mockMvc.perform(post("/company/offers")
                        .principal(mockPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertTrue(response.contains("Junior Java Developer"));

        verify(offerService).createOffer(any(OfferRequest.class), eq("rh@acme.com"));
        verifyNoMoreInteractions(offerService);
    }

    /**
     * Should return 400 Bad Request when the service reports invalid creation data.
     * {@link GlobalExceptionHandler} maps {@link IllegalArgumentException} to 400.
     *
     * @throws Exception if the HTTP call fails
     */
    @Test
    void shouldReturnBadRequestWhenCreateValidationFails() throws Exception {
        OfferRequest badRequest = new OfferRequest(
                "", // invalid title
                "",
                "not-a-url",
                "also-not-a-url"
        );

        when(offerService.createOffer(any(OfferRequest.class), eq("rh@acme.com")))
                .thenThrow(new IllegalArgumentException("Invalid offer data"));

        mockMvc.perform(post("/company/offers")
                        .principal(mockPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());

        verify(offerService).createOffer(any(OfferRequest.class), eq("rh@acme.com"));
        verifyNoMoreInteractions(offerService);
    }

    /**
     * Should return 200 OK and the updated offer when update succeeds.
     *
     * @throws Exception if the HTTP call fails
     */
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
        verifyNoMoreInteractions(offerService);
    }

    /**
     * Should return 400 Bad Request when the path variable is not a valid number on update.
     * <p>Example: PUT /company/offers/abc</p>
     *
     * @throws Exception if the HTTP call fails
     */
    @Test
    void shouldReturnBadRequestForInvalidIdTypeOnUpdate() throws Exception {
        OfferRequest request = new OfferRequest(
                "Any Title",
                "Any Description",
                "https://example.com/logo.png",
                "https://company.com"
        );

        mockMvc.perform(put("/company/offers/abc")
                        .principal(mockPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(offerService);
    }

    /**
     * Should return 400 Bad Request when the service reports invalid update data.
     * {@link GlobalExceptionHandler} maps {@link IllegalArgumentException} to 400.
     *
     * @throws Exception if the HTTP call fails
     */
    @Test
    void shouldReturnBadRequestWhenUpdateValidationFails() throws Exception {
        OfferRequest badUpdate = new OfferRequest(
                "", "", "bad-url", "bad-url"
        );

        when(offerService.updateOffer(eq(1L), any(OfferRequest.class), eq("rh@acme.com")))
                .thenThrow(new IllegalArgumentException("Invalid offer data"));

        mockMvc.perform(put("/company/offers/1")
                        .principal(mockPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badUpdate)))
                .andExpect(status().isBadRequest());

        verify(offerService).updateOffer(eq(1L), any(OfferRequest.class), eq("rh@acme.com"));
        verifyNoMoreInteractions(offerService);
    }

    /**
     * Should return 200 OK when deletion succeeds.
     *
     * @throws Exception if the HTTP call fails
     */
    @Test
    void shouldDeleteOffer() throws Exception {
        doNothing().when(offerService).deleteOffer(1L, "rh@acme.com");

        mockMvc.perform(delete("/company/offers/1")
                        .principal(mockPrincipal))
                .andExpect(status().isOk());

        verify(offerService).deleteOffer(1L, "rh@acme.com");
        verifyNoMoreInteractions(offerService);
    }

    /**
     * Should return 400 Bad Request when the path variable is not a valid number on delete.
     * <p>Example: DELETE /company/offers/abc</p>
     *
     * @throws Exception if the HTTP call fails
     */
    @Test
    void shouldReturnBadRequestForInvalidIdTypeOnDelete() throws Exception {
        mockMvc.perform(delete("/company/offers/abc")
                        .principal(mockPrincipal))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(offerService);
    }

    /**
     * Should return 200 OK with an empty JSON array when the company has no offers.
     *
     * @throws Exception if the HTTP call fails
     */
    @Test
    void shouldReturnEmptyListWhenNoOffers() throws Exception {
        when(offerService.getOffersByCompany("rh@acme.com")).thenReturn(List.of());

        MvcResult result = mockMvc.perform(get("/company/offers")
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andReturn();

        assertTrue("[]".equals(result.getResponse().getContentAsString().trim()));

        verify(offerService).getOffersByCompany("rh@acme.com");
        verifyNoMoreInteractions(offerService);
    }
}
