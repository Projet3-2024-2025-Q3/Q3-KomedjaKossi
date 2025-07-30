package com.example.jobappbackend;

import com.example.jobappbackend.controller.AdminUserController;
import com.example.jobappbackend.dto.RegisterRequest;
import com.example.jobappbackend.dto.UserResponse;
import com.example.jobappbackend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class AdminUserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AdminUserController adminUserController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private UserResponse sampleUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(adminUserController).build();
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

    @Test
    void shouldGetAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(sampleUser));

        MvcResult result = mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertTrue(response.contains("johnDoe"));
        verify(userService).getAllUsers();
    }

    @Test
    void shouldCreateUser() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "johnDoe", "john@example.com", "password123", "STUDENT",
                "John", "Doe", "123 Main St", null, "0123456789"
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
    }

    @Test
    void shouldUpdateUser() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "johnDoeUpdated", "john@example.com", "newPass123", "STUDENT",
                "John", "Doe", "456 Avenue", null, "0123456789"
        );

        when(userService.updateUser(eq(1L), any(RegisterRequest.class))).thenReturn(sampleUser);

        mockMvc.perform(put("/admin/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(userService).updateUser(eq(1L), any(RegisterRequest.class));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/admin/users/1"))
                .andExpect(status().isOk());

        verify(userService).deleteUser(1L);
    }
}
