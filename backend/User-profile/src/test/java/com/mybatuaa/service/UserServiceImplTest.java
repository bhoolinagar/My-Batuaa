package com.mybatuaa.service;

import com.mybatuaa.model.Role;
import com.mybatuaa.model.User;
import com.mybatuaa.model.Wallet;
import com.mybatuaa.repository.UserRepository;
import com.mybatuaa.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletService walletService;

    @Mock
    private WalletRepository walletRepository;

    private User user;

    @BeforeEach
    void setUp() {
        // Initialize mocks for each test
        MockitoAnnotations.openMocks(this);

        // Creating a sample user object for testing
        user = new User();
        user.setUserId(1);
        user.setName("Anjali");
        user.setEmailId("anjali@gmail.com");
        user.setPassword("9876543211");
        user.setMobileNumber("0199999999");
        user.setRole(Role.BUYER); // Assigning a role for testing
    }

    // User registration should succeed with valid details
    @Test
    void givenValidUser_whenRegister_thenSuccess() {
        // Simulate that email is not already registered
        when(userRepository.existsByEmailId(user.getEmailId())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(walletService.generateWalletId(anyString(), anyString())).thenReturn("WALLET123");

        // Call the service method
        User savedUser = userService.registerUser(user);

        // Check that user is saved and wallet is also created
        assertNotNull(savedUser);
        verify(userRepository).save(user);
        verify(walletRepository).save(any(Wallet.class));
    }

    //Registration should fail if email is already in use
    /*@Test
    void givenExistingEmail_whenRegister_thenThrowException() {
        // Simulate that user with this email already exists
        when(userRepository.existsByEmailId(user.getEmailId())).thenReturn(true);

        // Expect a RuntimeException to be thrown
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.registerUser(user));

        // Check exception message
        assertEquals("User already registered", ex.getMessage());

        // Ensure user is not saved again
        verify(userRepository, never()).save(any(User.class));
    }*/

    //Login should succeed if email, password and role match
    @Test
    void givenValidCredentials_whenLogin_thenReturnUser() {
        // Mock repository to return our sample user
        when(userRepository.findByEmailId("anjali@gmail.com")).thenReturn(Optional.of(user));

        // Try to login with correct credentials
        User loggedInUser = userService.login("anjali@gmail.com", "9876543211", Role.BUYER);

        // Check if returned user is correct
        assertNotNull(loggedInUser);
        assertEquals("anjali@gmail.com", loggedInUser.getEmailId());
        assertEquals(Role.BUYER, loggedInUser.getRole());
    }

    //Login should fail if password is incorrect
    @Test
    void givenWrongPassword_whenLogin_thenThrowException() {
        // Return a valid user, but we will use wrong password in test
        when(userRepository.findByEmailId(user.getEmailId())).thenReturn(Optional.of(user));

        // Expect an exception due to wrong password
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                userService.login(user.getEmailId(), "wrongpass", Role.BUYER));

        // Check error message
        assertEquals("Invalid email, password, or role", ex.getMessage());
    }

    // Login should fail if email does not exist in DB
    @Test
    void givenNonExistingEmail_whenLogin_thenThrowException() {
        // Simulate user not found
        when(userRepository.findByEmailId(user.getEmailId())).thenReturn(Optional.empty());

        // Expect exception due to user not found
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                userService.login(user.getEmailId(), "password", Role.BUYER));

        assertEquals("Invalid email, password, or role", ex.getMessage());
    }
}