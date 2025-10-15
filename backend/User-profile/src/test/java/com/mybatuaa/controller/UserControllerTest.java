package com.mybatuaa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybatuaa.exception.UnauthorizedAccessException;
import com.mybatuaa.model.Role;
import com.mybatuaa.model.User;
import com.mybatuaa.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)  // Only load controller, not full context
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc; // Used to simulate HTTP requests

    @MockBean
    private UserServiceImpl userService; // Mocking the service layer

    @Autowired
    private ObjectMapper objectMapper; // To convert Java object <-> JSON
    @InjectMocks
    private UserController userController;

    private User user;

    @BeforeEach
    void setUp() {
        // Creating a sample user object for testing
        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        user = new User();
        user.setUserId(1);
        user.setName("Anjali");
        user.setEmailId("anjali@gmail.com");
        user.setPassword("9876543211");
        user.setMobileNumber("0199999999");
        user.setRole(Role.BUYER); // Assigning a role for testing
    }

    @Test
    void givenValidUser_whenRegister_thenReturnsCreated() throws Exception {
        // Mocking the behavior of service
        when(userService.registerUser(any(User.class))).thenReturn(user);

        // Performing a POST request to /register with user data as JSON
        mockMvc.perform(post("/users/api/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated()) // Expecting 201 Created
                .andExpect(jsonPath("$.emailId").value("anjali@gmail.com"));
    }

    @Test
    void givenValidCredentials_whenLogin_thenReturnsUser() throws Exception {
        // Mock service response when correct login credentials are passed
        when(userService.login("anjali@gmail.com", "9876543211", Role.BUYER))
                .thenReturn(user);

        // GET request with query parameters (no JSON here)
        mockMvc.perform(get("/users/api/v1/login")
                        .param("emailId", "anjali@gmail.com")
                        .param("password", "9876543211")
                        .param("role", "BUYER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emailId").value("anjali@gmail.com"))
                .andExpect(jsonPath("$.role").value("BUYER"));
    }

    @Test
    void givenInvalidCredentials_whenLogin_thenReturnsUnauthorized() throws Exception {
        // Mock the service to throw your custom exception
        when(userService.login("anjali@gmail.com", "wrongpass", Role.BUYER))
                .thenThrow(new UnauthorizedAccessException("Invalid email, password, or role"));

        // Perform GET request to /login
        mockMvc.perform(get("/users/api/v1/login")
                        .param("emailId", "anjali@gmail.com")
                        .param("password", "wrongpass")
                        .param("role", "BUYER"))
                .andExpect(status().isUnauthorized()) // Expect HTTP 401
                .andExpect(content().string("Invalid email, password, or role")); // Expect message
    }

}