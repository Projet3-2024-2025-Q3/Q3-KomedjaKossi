package com.example.jobappbackend;

import com.example.jobappbackend.controller.AuthController;
import com.example.jobappbackend.dto.*;
import com.example.jobappbackend.model.User;
import com.example.jobappbackend.service.JwtService;
import com.example.jobappbackend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitaires pour le contrôleur d'authentification {@link AuthController}.
 */
@SpringBootTest
public class AuthControllerTest {

    /**
     * MockMvc pour simuler les requêtes HTTP.
     */
    private MockMvc mockMvc;

    /**
     * Mapper Jackson pour sérialiser/désérialiser les objets JSON.
     */
    private ObjectMapper objectMapper;

    /**
     * Mock du gestionnaire d'authentification.
     */
    @Mock
    public AuthenticationManager authManager;

    /**
     * Mock du service utilisateur.
     */
    @Mock
    private UserService userService;

    /**
     * Mock du service JWT.
     */
    @Mock
    private JwtService jwtService;

    /**
     * Contrôleur d'authentification testé.
     */
    @InjectMocks
    private AuthController authController;

    /**
     * Initialise les mocks et le contrôleur avant chaque test.
     */
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // On injecte les mocks manuellement dans le contrôleur
        authController = new AuthController(null);
        authController.authManager = authManager;
        authController.userService = userService;
        authController.jwtService = jwtService;

        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
    }

    /**
     * Test pour vérifier que l'enregistrement d'un utilisateur fonctionne correctement.
     */
    @Test
    public void shouldRegisterUserSuccessfully() throws Exception {
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

        MvcResult result = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        assertTrue(jsonResponse.contains("testuser"));
        assertTrue(jsonResponse.contains("test@example.com"));
        assertTrue(jsonResponse.contains("TestCompany"));
    }

    /**
     * Test pour vérifier que la connexion renvoie un token JWT si les identifiants sont valides.
     */
    @Test
    public void shouldLoginUserSuccessfully() throws Exception {
        AuthRequest request = new AuthRequest("testuser", "password123");

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("testuser")
                .password("encodedPassword")
                .roles("USER")
                .build();

        when(userService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtService.generateToken("testuser")).thenReturn("fake-jwt-token");

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        assertTrue(jsonResponse.contains("fake-jwt-token"));
    }
}
