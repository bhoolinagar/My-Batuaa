package com.batuaa.userprofile.controller;

import com.batuaa.userprofile.dto.BuyerDto;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BuyerController.class)
class BuyerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BuyerService buyerService;

    private ObjectMapper objectMapper;
    private BuyerDto buyerDto;
    private Buyer buyer;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        buyerDto = new BuyerDto();
        buyerDto.setEmailId("anjali@gmail.com");
        buyerDto.setName("Anjali Test");
        buyerDto.setPassword("password123");

        buyer = new Buyer();
        buyer.setEmailId(buyerDto.getEmailId().toLowerCase());
        buyer.setName(buyerDto.getName());
        buyer.setPassword(buyerDto.getPassword());
    }

    // Admin And Buyer registration testcase
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
                .andExpect(jsonPath("$.data.name").value("Anjali Test"))
                .andExpect(jsonPath("$.data.Role").value("BUYER"));
    }

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
                .andExpect(jsonPath("$.data.name").value("Anjali Test"))
                .andExpect(jsonPath("$.data.Role").value("ADMIN"));
    }


    @Test
    void testRegisterBuyer_NullRole_Failure() throws Exception {
        buyerDto.setRole(null);

        Mockito.when(buyerService.registerBuyer(any(BuyerDto.class)))
                .thenThrow(new RuntimeException("Role must be provided (ADMIN or BUYER)"));

        mockMvc.perform(post("/buyers/api/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buyerDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("fail"))
                .andExpect(jsonPath("$.message").value("Role must be provided (ADMIN or BUYER)"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    // Admin And Buyer login testcase
    @Test
    void testBuyerLoginSuccess() throws Exception {
        buyer.setRole(Role.BUYER);

        Mockito.when(buyerService.validateBuyer(any(BuyerDto.class))).thenReturn(buyer);

        mockMvc.perform(post("/buyers/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buyerDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Buyer login successful"))
                .andExpect(jsonPath("$.data.role").value("BUYER"))
                .andExpect(jsonPath("$.data.token").exists());
    }

    @Test
    void testAdminLoginSuccess() throws Exception {
        buyer.setRole(Role.ADMIN);

        Mockito.when(buyerService.validateBuyer(any(BuyerDto.class))).thenReturn(buyer);

        mockMvc.perform(post("/buyers/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buyerDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Admin login successful"))
                .andExpect(jsonPath("$.data.role").value("ADMIN"))
                .andExpect(jsonPath("$.data.token").exists());
    }

    @Test
    void testLoginInvalidCredentials() throws Exception {
        Mockito.when(buyerService.validateBuyer(any(BuyerDto.class))).thenReturn(null);

        mockMvc.perform(post("/buyers/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buyerDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("fail"))
                .andExpect(jsonPath("$.message").value("Invalid credentials"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}