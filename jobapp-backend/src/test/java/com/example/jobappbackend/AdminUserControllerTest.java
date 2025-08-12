package com.example.jobappbackend;

import com.example.jobappbackend.controller.AdminUserController;
import com.example.jobappbackend.dto.RegisterRequest;
import com.example.jobappbackend.dto.UserResponse;
import com.example.jobappbackend.exception.GlobalExceptionHandler;
import com.example.jobappbackend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for {@link AdminUserController} using standalone {@link MockMvc}.
 * <p>
 * These tests validate only the controller layer:
 * <ul>
 *     <li>All dependencies are mocked with Mockito.</li>
 *     <li>No Spring context is started.</li>
 *     <li>{@link GlobalExceptionHandler} is registered to ensure consistent HTTP status mapping.</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class AdminUserControllerTest {

    /** Mocked service responsible for user management operations. */
    @Mock
    private UserService userService;

    /** Controller under test with mocked dependencies injected by Mockito. */
    @InjectMocks
    private AdminUserController adminUserController;

    /** Standalone MockMvc client bound to the controller under test. */
    private MockMvc mockMvc;

    /** JSON serializer/deserializer used to build request bodies. */
    private ObjectMapper objectMapper;

    /** Reusable sample user returned by the mocked service. */
    private UserResponse sampleUser;

    /**
     * Initializes {@link MockMvc} with the controller and global exception handler.
     * Also prepares common fixtures used across tests.
     */
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(adminUserController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();

        sampleUser = new UserResponse(
                1L,
                "johnDoe",
                "john@example.com",
                "STUDENT",
                "John",
                "Doe",
                "123 Main St",
                null,
                "0123456789"
        );
    }

    /**
     * Should return 200 OK and the list of users.
     *
     * @throws Exception if the HTTP call fails
     */
    @Test
    void shouldGetAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(sampleUser));

        MvcResult result = mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertTrue(response.contains("johnDoe"));

        verify(userService).getAllUsers();
        verifyNoMoreInteractions(userService);
    }

    /**
     * Should return 200 OK and the created user when registration succeeds.
     *
     * @throws Exception if the HTTP call fails
     */
    @Test
    void shouldCreateUser() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "johnDoe",
                "john@example.com",
                "password123",
                "STUDENT",
                "John",
                "Doe",
                "123 Main St",
                null,
                "0123456789"
        );

        when(userService.register(any(RegisterRequest.class))).thenReturn(sampleUser);

        MvcResult result = mockMvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertTrue(response.contains("johnDoe"));

        verify(userService).register(any(RegisterRequest.class));
        verifyNoMoreInteractions(userService);
    }

    /**
     * Should return 200 OK and the updated user when update succeeds.
     *
     * @throws Exception if the HTTP call fails
     */
    @Test
    void shouldUpdateUser() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "johnDoeUpdated",
                "john@example.com",
                "newPass123",
                "STUDENT",
                "John",
                "Doe",
                "456 Avenue",
                null,
                "0123456789"
        );

        when(userService.updateUser(eq(1L), any(RegisterRequest.class))).thenReturn(sampleUser);

        mockMvc.perform(put("/admin/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(userService).updateUser(eq(1L), any(RegisterRequest.class));
        verifyNoMoreInteractions(userService);
    }

    /**
     * Should return 200 OK when deletion succeeds.
     *
     * @throws Exception if the HTTP call fails
     */
    @Test
    void shouldDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/admin/users/1"))
                .andExpect(status().isOk());

        verify(userService).deleteUser(1L);
        verifyNoMoreInteractions(userService);
    }

    /**
     * Should return 400 Bad Request when the path variable for update is not a valid number.
     * <p>
     * Example: PUT /admin/users/abc where a {@code Long} is expected.
     *
     * @throws Exception if the HTTP call fails
     */
    @Test
    void shouldReturnBadRequestForInvalidIdTypeOnUpdate() throws Exception {
        mockMvc.perform(put("/admin/users/abc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegisterRequest(
                                "u", "e@e.com", "p", "STUDENT", "f", "l", "addr", null, "000"
                        ))))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    /**
     * Should return 400 Bad Request when the path variable for delete is not a valid number.
     * <p>
     * Example: DELETE /admin/users/abc where a {@code Long} is expected.
     *
     * @throws Exception if the HTTP call fails
     */
    @Test
    void shouldReturnBadRequestForInvalidIdTypeOnDelete() throws Exception {
        mockMvc.perform(delete("/admin/users/abc"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    /**
     * Should return 400 Bad Request when registration fails due to invalid input
     * and the service throws {@link IllegalArgumentException}.
     *
     * @throws Exception if the HTTP call fails
     */
    @Test
    void shouldReturnBadRequestWhenCreateValidationFails() throws Exception {
        RegisterRequest badRequest = new RegisterRequest(
                "", // invalid username
                "invalid-email",
                "p",
                "STUDENT",
                "John",
                "Doe",
                "123 Main St",
                null,
                "0123456789"
        );

        when(userService.register(any(RegisterRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid data"));

        mockMvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest());

        verify(userService).register(any(RegisterRequest.class));
        verifyNoMoreInteractions(userService);
    }

    /**
     * Should return 400 Bad Request when update fails due to invalid input
     * and the service throws {@link IllegalArgumentException}.
     *
     * @throws Exception if the HTTP call fails
     */
    @Test
    void shouldReturnBadRequestWhenUpdateValidationFails() throws Exception {
        RegisterRequest badUpdate = new RegisterRequest(
                "johnDoe",
                "john@example.com",
                "", // invalid password
                "STUDENT",
                "John",
                "Doe",
                "123 Main St",
                null,
                "0123456789"
        );

        when(userService.updateUser(eq(1L), any(RegisterRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid data"));

        mockMvc.perform(put("/admin/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badUpdate)))
                .andExpect(status().isBadRequest());

        verify(userService).updateUser(eq(1L), any(RegisterRequest.class));
        verifyNoMoreInteractions(userService);
    }

    /**
     * Should return 200 OK with an empty JSON array when no users are found.
     *
     * @throws Exception if the HTTP call fails
     */
    @Test
    void shouldReturnEmptyListWhenNoUsersFound() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of());

        MvcResult result = mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertTrue("[]".equals(response) || "[]".equals(response.trim()));

        verify(userService).getAllUsers();
        verifyNoMoreInteractions(userService);
    }
}
