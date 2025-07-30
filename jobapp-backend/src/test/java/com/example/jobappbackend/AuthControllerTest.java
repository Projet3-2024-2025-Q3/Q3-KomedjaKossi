package com.example.jobappbackend;

import com.example.jobappbackend.dto.*;
import com.example.jobappbackend.model.User;
import com.example.jobappbackend.controller.AuthController;
import com.example.jobappbackend.service.JwtService;
import com.example.jobappbackend.service.UserService;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Unit tests for the {@link AuthController}.
 * Verifies the registration and login endpoints using MockMvc and Mockito.
 */
@SpringBootTest
class AuthControllerTest {

    /**
     * Mocked authentication manager used for simulating login authentication.
     */
    @Mock
    private AuthenticationManager authManager;

    /**
     * Mocked service for handling JWT operations.
     */
    @Mock
    private JwtService jwtService;

    /**
     * Mocked service for user operations (registration and loading).
     */
    @Mock
    private UserService userService;

    /**
     * Controller under test, with mocks injected.
     */
    @InjectMocks
    private AuthController authController;

    /**
     * MockMvc used to simulate HTTP requests to the controller.
     */
    private MockMvc mockMvc;

    /**
     * Jackson object mapper for serializing/deserializing JSON.
     */
    private ObjectMapper objectMapper;

    /**
     * Setup method to initialize MockMvc and ObjectMapper before each test.
     */
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
    }

    /**
     * Test case for successful user registration.
     *
     * @throws Exception if the request fails
     */
    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "testuser", "test@example.com", "password123", "USER",
                "John", "Doe", "123 Main St", "TestCompany", "0123456789"
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

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        assertTrue(jsonResponse.contains("testuser"));
        assertTrue(jsonResponse.contains("test@example.com"));
        assertTrue(jsonResponse.contains("TestCompany"));
    }


    /**
     * Test case for successful user login.
     *
     * @throws Exception if the request fails
     */
    @Test
    void shouldLoginUserSuccessfully() throws Exception {
        AuthRequest request = new AuthRequest("testuser", "password123");

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("testuser")
                .password("encodedPassword")
                .roles("USER")
                .build();

        when(userService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtService.generateToken("testuser")).thenReturn("fake-jwt-token");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertTrue(response.contains("fake-jwt-token"));
    }
}
