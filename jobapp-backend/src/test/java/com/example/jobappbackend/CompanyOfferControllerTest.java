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
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

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

    /** Reusable sample offer returned by the mocked service. */
    private OfferResponse sampleOffer;

    private static final long USER_ID = 101L;

    /**
     * Custom ArgumentResolver to inject JWT in @AuthenticationPrincipal parameter
     */
    private static class JwtArgumentResolver implements HandlerMethodArgumentResolver {
        private final Jwt jwt;

        public JwtArgumentResolver(Jwt jwt) {
            this.jwt = jwt;
        }

        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return parameter.hasParameterAnnotation(AuthenticationPrincipal.class) &&
                    parameter.getParameterType().equals(Jwt.class);
        }

        @Override
        public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
            return jwt;
        }
    }

    /**
     * Initializes {@link MockMvc}, registers the global exception handler, and prepares common fixtures.
     */
    @BeforeEach
    void setUp() {
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

        objectMapper = new ObjectMapper();
    }

    /**
     * Helper method to create MockMvc with a specific JWT
     */
    private MockMvc createMockMvcWithJwt(Long userId) {
        Jwt jwt = mock(Jwt.class);
        lenient().when(jwt.getClaim("userId")).thenReturn(userId);

        return MockMvcBuilders
                .standaloneSetup(companyOfferController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new JwtArgumentResolver(jwt))
                .build();
    }

    /**
     * Helper method to create MockMvc without JWT setup (for tests that fail before auth)
     */
    private MockMvc createMockMvcWithoutAuth() {
        return MockMvcBuilders
                .standaloneSetup(companyOfferController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    /**
     * Should return 200 OK and the list of offers for the authenticated company.
     *
     * @throws Exception if the HTTP call fails
     */
    @Test
    void shouldGetCompanyOffers() throws Exception {
        mockMvc = createMockMvcWithJwt(USER_ID);
        when(offerService.getOffersByCompany(USER_ID)).thenReturn(List.of(sampleOffer));

        MvcResult result = mockMvc.perform(get("/company/offers"))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertTrue(response.contains("Junior Java Developer"));

        verify(offerService).getOffersByCompany(USER_ID);
        verifyNoMoreInteractions(offerService);
    }

    /**
     * Should return 200 OK and the created offer when creation succeeds.
     *
     * @throws Exception if the HTTP call fails
     */
    @Test
    void shouldCreateOffer() throws Exception {
        mockMvc = createMockMvcWithJwt(USER_ID);
        OfferRequest request = new OfferRequest(
                "Junior Java Developer",
                "Great opportunity",
                "https://example.com/logo.png",
                "https://company.com"
        );

        when(offerService.createOffer(any(OfferRequest.class), eq(USER_ID))).thenReturn(sampleOffer);

        MvcResult result = mockMvc.perform(post("/company/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertTrue(response.contains("Junior Java Developer"));

        verify(offerService).createOffer(any(OfferRequest.class), eq(USER_ID));
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
        mockMvc = createMockMvcWithJwt(USER_ID);
        OfferRequest badRequest = new OfferRequest(
                "", // invalid title
                "",
                "not-a-url",
                "also-not-a-url"
        );

        when(offerService.createOffer(any(OfferRequest.class), eq(USER_ID)))
                .thenThrow(new IllegalArgumentException("Invalid offer data"));

        mockMvc.perform(post("/company/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());

        verify(offerService).createOffer(any(OfferRequest.class), eq(USER_ID));
        verifyNoMoreInteractions(offerService);
    }

    /**
     * Should return 200 OK and the updated offer when update succeeds.
     *
     * @throws Exception if the HTTP call fails
     */
    @Test
    void shouldUpdateOffer() throws Exception {
        mockMvc = createMockMvcWithJwt(USER_ID);
        OfferRequest request = new OfferRequest(
                "Updated Java Developer",
                "Updated description",
                "https://example.com/logo2.png",
                "https://company.com"
        );

        when(offerService.updateOffer(eq(1L), any(OfferRequest.class), eq(USER_ID)))
                .thenReturn(sampleOffer);

        mockMvc.perform(put("/company/offers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(offerService).updateOffer(eq(1L), any(OfferRequest.class), eq(USER_ID));
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
        mockMvc = createMockMvcWithoutAuth(); // No JWT needed - fails at path variable parsing
        OfferRequest request = new OfferRequest(
                "Any Title",
                "Any Description",
                "https://example.com/logo.png",
                "https://company.com"
        );

        mockMvc.perform(put("/company/offers/abc")
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
        mockMvc = createMockMvcWithJwt(USER_ID);
        OfferRequest badUpdate = new OfferRequest(
                "", "", "bad-url", "bad-url"
        );

        when(offerService.updateOffer(eq(1L), any(OfferRequest.class), eq(USER_ID)))
                .thenThrow(new IllegalArgumentException("Invalid offer data"));

        mockMvc.perform(put("/company/offers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badUpdate)))
                .andExpect(status().isBadRequest());

        verify(offerService).updateOffer(eq(1L), any(OfferRequest.class), eq(USER_ID));
        verifyNoMoreInteractions(offerService);
    }

    /**
     * Should return 200 OK when deletion succeeds.
     *
     * @throws Exception if the HTTP call fails
     */
    @Test
    void shouldDeleteOffer() throws Exception {
        mockMvc = createMockMvcWithJwt(USER_ID);
        doNothing().when(offerService).deleteOffer(1L, USER_ID);

        mockMvc.perform(delete("/company/offers/1"))
                .andExpect(status().isOk());

        verify(offerService).deleteOffer(1L, USER_ID);
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
        mockMvc = createMockMvcWithoutAuth(); // No JWT needed - fails at path variable parsing

        mockMvc.perform(delete("/company/offers/abc"))
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
        mockMvc = createMockMvcWithJwt(USER_ID);
        when(offerService.getOffersByCompany(USER_ID)).thenReturn(List.of());

        MvcResult result = mockMvc.perform(get("/company/offers"))
                .andExpect(status().isOk())
                .andReturn();

        assertTrue("[]".equals(result.getResponse().getContentAsString().trim()));

        verify(offerService).getOffersByCompany(USER_ID);
        verifyNoMoreInteractions(offerService);
    }

    /**
     * Should throw ApiException when JWT doesn't contain userId claim.
     * ApiException is mapped to 400 Bad Request in GlobalExceptionHandler.
     *
     * @throws Exception if the HTTP call fails
     */
    @Test
    void shouldThrowExceptionWhenUserIdMissing() throws Exception {
        // Create MockMvc with JWT that has no userId
        mockMvc = createMockMvcWithJwt(null);

        mockMvc.perform(get("/company/offers"))
                .andExpect(status().isBadRequest()); // ApiException maps to 400 in GlobalExceptionHandler

        verifyNoInteractions(offerService);
    }
}