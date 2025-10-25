package com.batuaa.userprofile.controller;

import com.batuaa.userprofile.dto.BuyerDto;
import com.batuaa.userprofile.dto.LoginDto;
import com.batuaa.userprofile.exception.LoginException;
import com.batuaa.userprofile.exception.RegistrationException;
import com.batuaa.userprofile.model.Buyer;
import com.batuaa.userprofile.model.Role;
import com.batuaa.userprofile.service.BuyerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BuyerController.class)
class BuyerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BuyerService buyerService;

    @Autowired
    private ObjectMapper objectMapper;

    private BuyerDto buyerDto;
    private Buyer buyer;
    private LoginDto loginDto;

    @BeforeEach
    void setUp() {
        buyerDto = new BuyerDto();
        buyerDto.setEmailId("anjali@gmail.com");
        buyerDto.setName("Anjali Test");
        buyerDto.setPassword("password123");

        buyer = new Buyer();
        buyer.setEmailId("anjali@gmail.com");
        buyer.setName("Anjali Test");
        buyer.setPassword("password123");

        loginDto = new LoginDto();
        loginDto.setEmailId("anjali@gmail.com");
        loginDto.setPassword("password123");
    }

    // Buyer Registration
    @Test
    void testRegisterBuyer_Success() throws Exception {
        buyerDto.setRole(Role.BUYER);
        buyer.setRole(Role.BUYER);

        Mockito.when(buyerService.registerBuyer(any(BuyerDto.class))).thenReturn(buyer);

        mockMvc.perform(post("/buyers/api/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buyerDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Buyer registered successfully"))
                .andExpect(jsonPath("$.data.emailId").value("anjali@gmail.com"))
                .andExpect(jsonPath("$.data.role").value("BUYER"));
    }

    //  Admin Registration
    @Test
    void testRegisterAdmin_Success() throws Exception {
        buyerDto.setRole(Role.ADMIN);
        buyer.setRole(Role.ADMIN);

        Mockito.when(buyerService.registerBuyer(any(BuyerDto.class))).thenReturn(buyer);

        mockMvc.perform(post("/buyers/api/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buyerDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Admin registered successfully"))
                .andExpect(jsonPath("$.data.emailId").value("anjali@gmail.com"))
                .andExpect(jsonPath("$.data.role").value("ADMIN"));
    }

    //Email already exists
    @Test
    void testRegisterBuyer_EmailExists_Failure() throws Exception {
        buyerDto.setRole(Role.BUYER);

        doThrow(new RegistrationException("Email already registered"))
                .when(buyerService).registerBuyer(any(BuyerDto.class));

        mockMvc.perform(post("/buyers/api/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buyerDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("fail"))
                .andExpect(jsonPath("$.message").value("Email already registered"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    // Buyer Login
    @Test
    void testBuyerLogin_Success() throws Exception {
        buyer.setRole(Role.BUYER);

        Mockito.when(buyerService.validateBuyer(any(LoginDto.class))).thenReturn(buyer);

        mockMvc.perform(post("/buyers/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Buyer login successful"))
                .andExpect(jsonPath("$.data.role").value("BUYER"))
                .andExpect(jsonPath("$.data.token").exists());
    }

    //  Admin Login
    @Test
    void testAdminLogin_Success() throws Exception {
        buyer.setRole(Role.ADMIN);

        Mockito.when(buyerService.validateBuyer(any(LoginDto.class))).thenReturn(buyer);

        mockMvc.perform(post("/buyers/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Admin login successful"))
                .andExpect(jsonPath("$.data.role").value("ADMIN"))
                .andExpect(jsonPath("$.data.token").exists());
    }


    @Test
    void testLogin_InvalidCredentials_Failure() throws Exception {
        doThrow(new LoginException("Invalid email or password"))
                .when(buyerService).validateBuyer(any(LoginDto.class));

        mockMvc.perform(post("/buyers/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("fail"))
                .andExpect(jsonPath("$.message").value("Invalid email or password"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}
