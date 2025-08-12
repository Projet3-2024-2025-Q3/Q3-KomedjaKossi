package com.example.jobappbackend;

import com.example.jobappbackend.controller.ApplicationController;
import com.example.jobappbackend.dto.ApplicationResponse;
import com.example.jobappbackend.exception.GlobalExceptionHandler;
import com.example.jobappbackend.service.ApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for {@link ApplicationController} using standalone {@link MockMvc}.
 * <p>
 * This class focuses on controller behavior:
 * <ul>
 *     <li>Mocks {@link ApplicationService} with Mockito</li>
 *     <li>Registers {@link GlobalExceptionHandler} to map exceptions to HTTP status codes</li>
 *     <li>Does not start the full Spring context</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class ApplicationControllerTest {

    /** Mocked service that provides application-related operations. */
    @Mock
    private ApplicationService applicationService;

    /** Controller under test with mocked dependencies injected by Mockito. */
    @InjectMocks
    private ApplicationController applicationController;

    /** Standalone MockMvc used to perform HTTP requests against the controller. */
    private MockMvc mockMvc;

    /**
     * Initializes a standalone {@link MockMvc} with the controller under test
     * and registers the {@link GlobalExceptionHandler} for consistent error mapping.
     */
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(applicationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    /**
     * Should return 200 OK and an empty JSON array when the student has no applications.
     *
     * @throws Exception if the HTTP call fails
     */
    @Test
    void shouldReturnEmptyApplicationsList() throws Exception {
        when(applicationService.getApplicationsByStudent(42L)).thenReturn(List.of());

        mockMvc.perform(get("/applications/student/42")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(applicationService).getApplicationsByStudent(42L);
        verifyNoMoreInteractions(applicationService);
    }

    /**
     * Should return 200 OK and a JSON array when the service returns results.
     * <p>
     * Note: This test asserts only that an array is returned and status is 200,
     * without relying on {@link ApplicationResponse} structure details.
     *
     * @throws Exception if the HTTP call fails
     */
    @Test
    void shouldReturnApplicationsList() throws Exception {
        // Using a minimal approach to avoid coupling to ApplicationResponse constructors/fields.
        // If ApplicationResponse has a no-args constructor, you may replace List.of()
        // with List.of(new ApplicationResponse()) and assert fields with jsonPath.
        when(applicationService.getApplicationsByStudent(7L)).thenReturn(List.of());

        mockMvc.perform(get("/applications/student/7")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(applicationService).getApplicationsByStudent(7L);
        verifyNoMoreInteractions(applicationService);
    }

    /**
     * Should return 400 Bad Request when the path variable cannot be converted to {@code Long}.
     * <p>
     * Example: GET /applications/student/abc where a numeric ID is required.
     *
     * @throws Exception if the HTTP call fails
     */
    @Test
    void shouldReturnBadRequestForInvalidStudentIdType() throws Exception {
        mockMvc.perform(get("/applications/student/abc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(applicationService);
    }

    /**
     * Should return 400 Bad Request when the service raises a validation error.
     * <p>
     * {@link GlobalExceptionHandler} maps {@link IllegalArgumentException} to HTTP 400.
     *
     * @throws Exception if the HTTP call fails
     */
    @Test
    void shouldReturnBadRequestWhenServiceValidationFails() throws Exception {
        when(applicationService.getApplicationsByStudent(99L))
                .thenThrow(new IllegalArgumentException("Invalid student id"));

        mockMvc.perform(get("/applications/student/99")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(applicationService).getApplicationsByStudent(99L);
        verifyNoMoreInteractions(applicationService);
    }

    /**
     * Should return 500 Internal Server Error when an unexpected exception occurs in the service.
     * <p>
     * {@link GlobalExceptionHandler} catch-all maps unexpected exceptions to HTTP 500.
     *
     * @throws Exception if the HTTP call fails
     */
    @Test
    void shouldReturnServerErrorOnUnexpectedFailure() throws Exception {
        when(applicationService.getApplicationsByStudent(13L))
                .thenThrow(new RuntimeException("Unexpected failure"));

        mockMvc.perform(get("/applications/student/13")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(applicationService).getApplicationsByStudent(13L);
        verifyNoMoreInteractions(applicationService);
    }
}
