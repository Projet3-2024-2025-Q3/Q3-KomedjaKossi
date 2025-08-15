package com.example.jobappbackend;

import com.example.jobappbackend.controller.AuthController;
import com.example.jobappbackend.dto.*;
import com.example.jobappbackend.exception.GlobalExceptionHandler;
import com.example.jobappbackend.model.User;
import com.example.jobappbackend.service.AuthService;
import com.example.jobappbackend.service.JwtService;
import com.example.jobappbackend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.jobappbackend.repository.UserRepository;
import java.util.Optional;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;



import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for {@link AuthController} using standalone {@link MockMvc}.
 * <p>
 * Controller is built manually with explicit mock injection to avoid NPEs.
 * {@link GlobalExceptionHandler} is registered to map exceptions to HTTP status codes.
 */
class AuthControllerTest {

    /** Mocked authentication manager that validates user credentials. */
    @Mock
    private AuthenticationManager authManager;

    /** Mocked user domain service used for registration and user lookups. */
    @Mock
    private UserService userService;

    /** Mocked JWT service that generates tokens for authenticated users. */
    @Mock
    private JwtService jwtService;

    /** Mocked authentication domain service for password reset/change flows. */
    @Mock
    private AuthService authService;

    @Mock
    public UserRepository userRepository;

    /** Controller under test. Dependencies are injected manually in {@link #setUp()}. */
    private AuthController authController;

    /** Standalone MockMvc bound to the controller under test. */
    private MockMvc mockMvc;

    /** JSON serializer used to build request payloads. */
    private ObjectMapper objectMapper;

    /**
     * Initializes Mockito, builds the controller with explicit mock wiring,
     * and registers {@link GlobalExceptionHandler} for consistent error mapping.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Explicit wiring avoids null fields in the controller during standalone tests
        authController = new AuthController(authService);
        authController.authManager = authManager;
        authController.userService = userService;
        authController.jwtService = jwtService;
        authController.userRepository = userRepository;

        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
    }

    /**
     * Should return 200 OK and the created user when registration succeeds.
     *
     * @throws Exception if the HTTP call fails
     */
    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "testuser",
                "test@example.com",
                "password123",
                "USER",
                "John",
                "Doe",
                "123 Main St",
                "TestCompany",
                "0123456789"
        );

        UserResponse responseMock = new UserResponse(
                1L,
                "testuser",
                "test@example.com",
                "USER",
                "John",
                "Doe",
                "123 Main St",
                "TestCompany",
                "0123456789"
        );

        when(userService.register(any(RegisterRequest.class))).thenReturn(responseMock);

        MvcResult result = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        assertTrue(json.contains("testuser"));
        assertTrue(json.contains("test@example.com"));
        assertTrue(json.contains("TestCompany"));

        verify(userService).register(any(RegisterRequest.class));
        verifyNoMoreInteractions(userService, jwtService, authService, authManager);
    }
    /**
     * Should return 200 OK and a JWT token when login succeeds.
     * <p>
     * Stubs {@link AuthenticationManager#authenticate} to return a token to prevent NPEs,
     * then mocks user lookup and JWT generation.
     *
     * @throws Exception if the HTTP call fails
     */
    @Test
    void shouldLoginUserSuccessfully() throws Exception {
        AuthRequest request = new AuthRequest("testuser", "password123");

        // Créer un mock User entity pour correspondre à ce que retourne userRepository.findByUsername()
        User mockUserEntity = new User();
        mockUserEntity.setUsername("testuser");
        mockUserEntity.setEmail("test@example.com");
        // Configurez les autres propriétés si nécessaire

        when(authManager.authenticate(any()))
                .thenReturn(new UsernamePasswordAuthenticationToken("testuser", "password123"));

        // Mock du UserRepository pour retourner l'entité User
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUserEntity));

        // Mock du JwtService avec l'objet User complet
        when(jwtService.generateToken(any(User.class))).thenReturn("fake-jwt-token");

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String json = result.getResponse().getContentAsString();
        assertTrue(json.contains("fake-jwt-token"));

        verify(authManager).authenticate(any());
        verify(userRepository).findByUsername("testuser");
        verify(jwtService).generateToken(any(User.class));
        verifyNoMoreInteractions(authManager, userRepository, jwtService, authService);
    }

    /**
     * Should return 401 Unauthorized when credentials are invalid.
     * {@link GlobalExceptionHandler} maps {@link org.springframework.security.core.AuthenticationException} to 401.
     *
     * @throws Exception if the HTTP call fails
     */
    @Test
    void shouldReturnUnauthorizedWhenLoginFails() throws Exception {
        AuthRequest request = new AuthRequest("baduser","badpass");

        when(authManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(authManager).authenticate(any());
        verifyNoInteractions(userService, jwtService, authService);
    }
    /**
     * Should return 200 OK and a confirmation message when password reset succeeds.
     *
     * @throws Exception if the HTTP call fails
     */
    @Test
    void shouldSendForgotPasswordEmail() throws Exception {
        doNothing().when(authService).resetPassword("john@example.com");

        mockMvc.perform(post("/auth/forgot-password")
                        .param("email", "john@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("A new password has been sent to your email."));

        verify(authService).resetPassword("john@example.com");
        verifyNoMoreInteractions(authService, userService, jwtService, authManager);
    }

    /**
     * Should return 500 Internal Server Error when email sending fails during password reset.
     * {@link GlobalExceptionHandler} maps {@link MessagingException} to HTTP 500.
     *
     * @throws Exception if the HTTP call fails
     */
    @Test
    void shouldReturnServerErrorWhenForgotPasswordEmailFails() throws Exception {
        doThrow(new MessagingException("SMTP error")).when(authService).resetPassword("broken@example.com");

        mockMvc.perform(post("/auth/forgot-password")
                        .param("email", "broken@example.com"))
                .andExpect(status().isInternalServerError());

        verify(authService).resetPassword("broken@example.com");
        verifyNoMoreInteractions(authService, userService, jwtService, authManager);
    }

    /**
     * Should return 405 Method Not Allowed when using POST instead of PUT for password change.
     * {@link GlobalExceptionHandler} maps {@link org.springframework.web.HttpRequestMethodNotSupportedException} to 405.
     *
     * @throws Exception if the HTTP call fails
     */
    @Test
    void shouldRejectChangePasswordWithWrongMethod() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("user", "old", "new");

        mockMvc.perform(post("/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isMethodNotAllowed());

        verifyNoInteractions(authService, userService, jwtService, authManager);
    }

    /**
     * Should return 200 OK and a confirmation message when password change succeeds via PUT.
     *
     * @throws Exception if the HTTP call fails
     */
    @Test
    void shouldChangePasswordSuccessfullyWithPut() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("testuser", "oldPass123", "newPass456");

        doNothing().when(authService).changePassword(any(ChangePasswordRequest.class));

        mockMvc.perform(put("/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Password changed successfully."));

        verify(authService).changePassword(any(ChangePasswordRequest.class));
        verifyNoMoreInteractions(authService, userService, jwtService, authManager);
    }
}
