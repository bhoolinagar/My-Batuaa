package com.batuaa.userprofile.service;

import com.batuaa.userprofile.dto.BuyerDto;
import com.batuaa.userprofile.dto.LoginDto;
import com.batuaa.userprofile.model.Buyer;
import com.batuaa.userprofile.model.Role;
import com.batuaa.userprofile.repository.BuyerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class BuyerServiceImplTest {

    @Mock
    private BuyerRepository buyerRepository;

    @InjectMocks
    private BuyerServiceImpl buyerService;

    private BuyerDto buyerDto;
    private Buyer buyer;
    private LoginDto loginDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        buyerDto = new BuyerDto();
        buyerDto.setEmailId("anjali@gmail.com");
        buyerDto.setName("Anjali Test");
        buyerDto.setPassword("password123");
        buyerDto.setRole(Role.BUYER);

        buyer = new Buyer();
        buyer.setEmailId(buyerDto.getEmailId().toLowerCase());
        buyer.setName(buyerDto.getName());
        buyer.setPassword(buyerDto.getPassword());
        buyer.setRole(Role.BUYER);

        loginDto = new LoginDto();
        loginDto.setEmailId("anjali@gmail.com");
        loginDto.setPassword("password123");
    }

    // Register Tests
    @Test
    void testRegisterBuyer_BuyerRole_Success() {
        when(buyerRepository.existsByEmailIdIgnoreCase(anyString())).thenReturn(false);
        when(buyerRepository.save(any(Buyer.class))).thenReturn(buyer);

        Buyer savedBuyer = buyerService.registerBuyer(buyerDto);

        assertNotNull(savedBuyer);
        assertEquals(Role.BUYER, savedBuyer.getRole());
        verify(buyerRepository, times(1)).save(any(Buyer.class));
    }

    @Test
    void testRegisterBuyer_AdminRole_Success() {
        buyerDto.setRole(Role.ADMIN);
        buyer.setRole(Role.ADMIN);

        when(buyerRepository.existsByEmailIdIgnoreCase(anyString())).thenReturn(false);
        when(buyerRepository.save(any(Buyer.class))).thenReturn(buyer);

        Buyer savedBuyer = buyerService.registerBuyer(buyerDto);

        assertNotNull(savedBuyer);
        assertEquals(Role.ADMIN, savedBuyer.getRole());
        verify(buyerRepository, times(1)).save(any(Buyer.class));
    }

    @Test
    void testRegisterBuyer_NullRole_Failure() {
        buyerDto.setRole(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            buyerService.registerBuyer(buyerDto);
        });

        assertEquals("Role must be provided (ADMIN or BUYER)", exception.getMessage());
        verify(buyerRepository, never()).save(any(Buyer.class));
    }

    @Test
    void testRegisterBuyer_DuplicateEmail_Failure() {
        when(buyerRepository.existsByEmailIdIgnoreCase(anyString())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            buyerService.registerBuyer(buyerDto);
        });

        assertEquals("Email already registered", exception.getMessage());
        verify(buyerRepository, never()).save(any(Buyer.class));
    }

    //  Validate Buyer Tests
    @Test
    void testValidateBuyer_CorrectCredentials_ReturnsBuyer() {
        buyer.setRole(Role.BUYER); // ensure role is set

        when(buyerRepository.findByEmailIdAndPassword(anyString(), anyString()))
                .thenReturn(Optional.of(buyer));

        Buyer result = buyerService.validateBuyer(loginDto);

        assertNotNull(result);
        assertEquals("Anjali Test", result.getName());
        assertEquals(Role.BUYER, result.getRole());
    }


    @Test
    void testValidateBuyer_WrongPassword_ThrowsLoginException() {
        when(buyerRepository.findByEmailIdAndPassword(anyString(), anyString()))
                .thenReturn(Optional.empty());

        loginDto.setPassword("wrongPassword");

        assertThrows(com.batuaa.userprofile.exception.LoginException.class, () -> {
            buyerService.validateBuyer(loginDto);
        });
    }


    @Test
    void testValidateBuyer_NonExistentEmail_ThrowsLoginException() {
        when(buyerRepository.findByEmailIdAndPassword(anyString(), anyString()))
                .thenReturn(Optional.empty());

        assertThrows(com.batuaa.userprofile.exception.LoginException.class, () -> {
            buyerService.validateBuyer(loginDto);
        });
    }

}
