package com.batuaa.userprofile.controller;

import com.batuaa.userprofile.dto.WalletDto;
import com.batuaa.userprofile.exception.BuyerNotFoundException;
import com.batuaa.userprofile.exception.GlobalExceptionHandler;
import com.batuaa.userprofile.exception.WalletAlreadyFound;
import com.batuaa.userprofile.exception.WalletNotFoundException;
import com.batuaa.userprofile.model.Buyer;
import com.batuaa.userprofile.model.Gender;
import com.batuaa.userprofile.model.Role;
import com.batuaa.userprofile.model.Wallet;
import com.batuaa.userprofile.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class WalletControllerTest {

    private MockMvc mockMvc;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private WalletController walletController;

    private WalletDto walletDto;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

// Attach GlobalExceptionHandler to MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(walletController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        walletDto = new WalletDto();
        walletDto.setEmailId("bhooli@gmail.com");
        walletDto.setAccountNumber("AC1234");
        walletDto.setBankName("HDFC Bank");
        walletDto.setBalance(new BigDecimal("1000"));

        Buyer buyer = new Buyer();
        buyer.setEmailId("bhooli@gmail.com");
        buyer.setName("Bhooli");
        buyer.setGender(Gender.FEMALE);
        buyer.setRole(Role.BUYER);

        wallet = new Wallet();
        wallet.setWalletId("WALB7A0E0285");
        wallet.setAccountNumber("AC1234");
        wallet.setBankName(walletDto.getBankName());
        wallet.setBalance(new BigDecimal("1000"));
        wallet.setBuyer(buyer);
        wallet.setCreatedAt(LocalDateTime.now());
    }

    // -- Link Bank Account --
    @Test
    void linkBankAccount_success() throws Exception {
        when(walletService.linkBankAccountToWallet(any(WalletDto.class)))
                .thenReturn("WALB7A0E0285");

        String walletJson = "{"
                + "\"emailId\": \"bhooli@gmail.com\","
                + "\"balance\": 1000,"
                + "\"bankName\": \"HDFC Bank\","
                + "\"accountNumber\": \"12345678901\""
                + "}";

        mockMvc.perform(post("/wallet/api/v1/link-bank-account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(walletJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").value("WALB7A0E0285"));
    }

    @Test
    void linkBankAccount_walletAlreadyExists() throws Exception {
        when(walletService.linkBankAccountToWallet(any(WalletDto.class)))
                .thenThrow(new WalletAlreadyFound("Bank account already linked with wallet"));

        String walletJson = "{"
                + "\"emailId\": \"bhooli@gmail.com\","
                + "\"balance\": 1000,"
                + "\"bankName\": \"HDFC Bank\","
                + "\"accountNumber\": \"12345678901\""
                + "}";

        mockMvc.perform(post("/wallet/api/v1/link-bank-account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(walletJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("fail"))
                .andExpect(jsonPath("$.message").value("Bank account already linked with wallet"));
    }

    @Test
    void linkBankAccount_failure_buyerNotFound() throws Exception {
        // Mock service to throw BuyerNotFoundException when buyer doesn't exist
        when(walletService.linkBankAccountToWallet(any(WalletDto.class)))
                .thenThrow(new BuyerNotFoundException("Buyer not found with email: invalid@gmail.com"));

        String walletJson = "{"
                + "\"emailId\": \"invalid@gmail.com\","
                + "\"balance\": 1000,"
                + "\"bankName\": \"HDFC Bank\","
                + "\"accountNumber\": \"12345678901\""
                + "}";
        // Expect 404 Not Found
        mockMvc.perform(post("/wallet/api/v1/link-bank-account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(walletJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("fail"))
                .andExpect(jsonPath("$.message").value("Buyer not found with email: invalid@gmail.com"));
    }

    // -- Add Money to Wallet --
    @Test
    void addMoneyToWallet_success() throws Exception {
        String walletId = "WALB7A0E0285";
        BigDecimal amount = new BigDecimal("500");

        when(walletService.updateMoneyFromBank(walletId, amount))
                .thenReturn("Money successfully added into wallet");

        mockMvc.perform(post("/wallet/api/v1/add-money")
                        .param("walletId", walletId)
                        .param("amount", amount.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Money successfully added into wallet"));
    }

    @Test
    void addMoneyToWallet_walletNotFound() throws Exception {
        String walletId = "INVALID_WALLET";
        BigDecimal amount = new BigDecimal("500");

        // Mock service to throw WalletNotFoundException
        when(walletService.updateMoneyFromBank(walletId, amount))
                .thenThrow(new WalletNotFoundException("Wallet not found with ID: " + walletId));

        // Perform POST request and expect 404 Not Found
        mockMvc.perform(post("/wallet/api/v1/add-money")
                        .param("walletId", walletId)
                        .param("amount", amount.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("fail"))
                .andExpect(jsonPath("$.message").value("Wallet not found with ID: " + walletId));
    }

    @Test
    void addMoneyToWallet_invalidAmount() throws Exception {
        String walletId = "WALB7A0E0285";
        BigDecimal amount = new BigDecimal("-100"); // negative amount

        // Mock service to throw IllegalArgumentException for invalid amount
        when(walletService.updateMoneyFromBank(walletId, amount))
                .thenThrow(new IllegalArgumentException("Amount must be greater than zero"));

        // Perform POST request and expect 400 Bad Request
        mockMvc.perform(post("/wallet/api/v1/add-money")
                        .param("walletId", walletId)
                        .param("amount", amount.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("fail"))
                .andExpect(jsonPath("$.message").value("Amount must be greater than zero"));
    }

    // --- Get Wallet Details ---
    @Test
    void getWalletDetails_success() throws Exception {
        String walletId = "WALB7A0E0285";

        when(walletService.getWalletDetails(walletId))
                .thenReturn(wallet);

        mockMvc.perform(get("/wallet/api/v1/details/{walletId}", walletId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Wallet details fetched successfully"))
                .andExpect(jsonPath("$.data.walletId").value(walletId))
                .andExpect(jsonPath("$.data.balance").value(1000));
    }
    @Test
    void getWalletDetails_walletNotFound() throws Exception {
        String walletId = "INVALID_WALLET";

        when(walletService.getWalletDetails(walletId))
                .thenThrow(new WalletNotFoundException("Wallet not found with ID: " + walletId));

        mockMvc.perform(get("/wallet/api/v1/details/{walletId}", walletId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("fail"))
                .andExpect(jsonPath("$.message").value("Wallet not found with ID: " + walletId));
    }

    // to get list of wallets by email id
    @Test
    void testGetWalletListByBuyer_Success() throws Exception {
        when(walletService.getWalletListByBuyer("bhooli@gmail.com"))
                .thenReturn(Collections.singletonList(wallet));

        mockMvc.perform(get("/wallet/api/v1/wallet-list/bhooli@gmail.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data[0].walletId").value("WALB7A0E0285"))
                .andExpect(jsonPath("$.data[0].accountNumber").value("AC1234"))
                .andExpect(jsonPath("$.data[0].balance").value(1000))
                .andExpect(jsonPath("$.data[0].bankName").value("HDFC Bank"))
                .andExpect(jsonPath("$.data[0].buyerEmail").value("bhooli@gmail.com"))
                .andExpect(jsonPath("$.data[0].buyerName").value("Bhooli"));

        verify(walletService, times(1)).getWalletListByBuyer("bhooli@gmail.com");
    }

    @Test
    void testGetWalletListByBuyer_EmptyList() throws Exception {
        when(walletService.getWalletListByBuyer("empty@gmail.com"))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/wallet/api/v1/wallet-list/empty@gmail.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isEmpty());

        verify(walletService, times(1)).getWalletListByBuyer("empty@gmail.com");
    }

    @Test
    void testGetWalletListByBuyer_BuyerNotFound() throws Exception {
        when(walletService.getWalletListByBuyer("unknown@gmail.com"))
                .thenThrow(new RuntimeException("Buyer not found"));

        mockMvc.perform(get("/wallet/api/v1/wallet-list/unknown@gmail.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Something went wrong: Buyer not found"));

        verify(walletService, times(1)).getWalletListByBuyer("unknown@gmail.com");
    }

    // to set primary wallet based on walletid end email
    @Test
    void testSetPrimaryWallet_Success() throws Exception {
        mockMvc.perform(put("/wallet/api/v1/set-primary")
                        .param("walletId", "WAL123")
                        .param("email", "priyanka@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Primary wallet updated successfully"));
    }

    // Test: getPrimaryWallet success
    @Test
    void testGetPrimaryWallet_Success() throws Exception {
        Wallet wallet = new Wallet();
        Buyer buyer= new Buyer();
        buyer.setEmailId("priyanka@gmail.com");
        wallet.setWalletId("WAL123");
        wallet.setBuyer(buyer);
        wallet.setPrimary(true);

        Mockito.when(walletService.getPrimaryWallet(anyString())).thenReturn(wallet);

        mockMvc.perform(get("/wallet/api/v1/primary")
                        .param("email", "priyanka@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.walletId").value("WAL123"))
                .andExpect(jsonPath("$.message").value(" primary wallet fetched successfully"));
    }


}