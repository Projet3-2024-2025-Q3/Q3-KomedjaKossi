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

@SpringBootTest
class AuthControllerTest {

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        RegisterRequest request = new RegisterRequest("testuser", "test@example.com", "password123", "USER");

        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setRole("USER");

        when(userService.register(any(RegisterRequest.class))).thenReturn(user);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertTrue(response.contains("testuser"));
    }

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
