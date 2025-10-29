package com.batuaa.userprofile.service;

import com.batuaa.userprofile.dto.WalletDto;
import com.batuaa.userprofile.service.WalletServiceImpl;
import com.batuaa.userprofile.exception.BuyerNotFoundException;
import com.batuaa.userprofile.exception.PrimaryWalletNotfound;
import com.batuaa.userprofile.exception.WalletAlreadyFound;
import com.batuaa.userprofile.exception.WalletNotFoundException;
import com.batuaa.userprofile.model.Buyer;
import com.batuaa.userprofile.model.Gender;
import com.batuaa.userprofile.model.Role;
import com.batuaa.userprofile.model.Wallet;
import com.batuaa.userprofile.repository.BuyerRepository;
import com.batuaa.userprofile.repository.WalletRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Slf4j
class WalletServiceImplTest {

    @Mock
    private WalletRepository walletRepository;
    @Mock
    private BuyerRepository buyerRepository;
    @InjectMocks
    private WalletServiceImpl walletService;

    private WalletDto walletDto;
    private Buyer buyer;
    private Wallet wallet;
    private Wallet wallet1;
    private Wallet wallet2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        walletDto = new WalletDto();
        walletDto.setEmailId("bhooli123@gmail.com");
        walletDto.setAccountNumber("AC1234");
        walletDto.setBalance(new BigDecimal("1000"));
        walletDto.setBankName("HDFC Bank");

        buyer = new Buyer();
        buyer.setEmailId("bhooli123@gmail.com");
        buyer.setName("Bhooli");
        buyer.setGender(Gender.FEMALE);
        buyer.setPassword("password123");
        buyer.setRole(Role.BUYER);

        // Wallet
        wallet = new Wallet();
        wallet.setWalletId("WAL123");
        wallet.setAccountNumber("21091234111");
        wallet.setBankName("HDFC Bank");
        wallet.setBalance(new BigDecimal("1000"));
        wallet.setBuyer(buyer);


        wallet1 = new Wallet();
        wallet1.setWalletId("WAL123");
        wallet1.setBuyer(buyer);
        wallet1.setPrimary(true);

        wallet2 = new Wallet();
        wallet2.setWalletId("WAL456");
        wallet2.setBuyer(buyer);
        wallet2.setPrimary(false);
    }

    @Test
    void generateWalletId() {
    }

    @Test
    void getWalletDetails() {
    }

    @Test
    void linkBankAccountToWallet_success() {
        // Mock: Buyer exists and wallet does not exist
        when(buyerRepository.findByEmailId(walletDto.getEmailId())).thenReturn(Optional.of(buyer));
        when(walletRepository.existsByWalletId(anyString())).thenReturn(false);
        when(walletRepository.existsByBuyerEmailIdAndAccountNumber(walletDto.getEmailId(), walletDto.getAccountNumber()))
                .thenReturn(false);

        String walletId = walletService.linkBankAccountToWallet(walletDto);

        // Assertions
        assertNotNull(walletId);
        // assertTrue(walletId.startsWith("WAL"));          // Wallet ID prefix
        // assertEquals(12, walletId.length());             // 12-character ID

        // Verify save is called
        verify(walletRepository, times(1)).save(any(Wallet.class));
    }

    @Test
    void linkBankAccountToWallet_walletAlreadyExists() {
        when(walletRepository.existsByBuyerEmailIdAndAccountNumber(walletDto.getEmailId(), walletDto.getAccountNumber()))
                .thenReturn(true);

        WalletAlreadyFound exception = assertThrows(WalletAlreadyFound.class, () -> {
            walletService.linkBankAccountToWallet(walletDto);
        });

        assertEquals("Bank account already linked with a wallet", exception.getMessage());
    }

    @Test
    void linkBankAccountToWallet_buyerNotFound() {
        when(buyerRepository.findByEmailId(walletDto.getEmailId())).thenReturn(Optional.empty());

        BuyerNotFoundException exception = assertThrows(BuyerNotFoundException.class, () -> {
            walletService.linkBankAccountToWallet(walletDto);
        });

        assertEquals("Buyer not registered with this email ID", exception.getMessage());
    }

    @Test
    void updateMoneyFromBank_success() {
        // Arrange
        String walletId = "WALB7A0E0285";
        BigDecimal amountToAdd = new BigDecimal("500");
        Wallet existingWallet = new Wallet();
        existingWallet.setWalletId(walletId);
        existingWallet.setBalance(new BigDecimal("1000"));

        when(walletRepository.findByWalletId(walletId)).thenReturn(Optional.of(existingWallet));

        // Act
        String result = walletService.updateMoneyFromBank(walletId, amountToAdd);

        // Assert
        assertEquals("Money successfully added into wallet. New Balance: 1500", result);
        assertEquals(new BigDecimal("1500"), existingWallet.getBalance());
        verify(walletRepository, times(1)).save(existingWallet);
    }

    @Test
    void updateMoneyFromBank_walletNotFound() {
        String walletId = "INVALID_WALLET";
        BigDecimal amount = new BigDecimal("500");

        when(walletRepository.findByWalletId(walletId)).thenReturn(Optional.empty());

        WalletNotFoundException exception = assertThrows(WalletNotFoundException.class, () -> {
            walletService.updateMoneyFromBank(walletId, amount);
        });

        assertEquals("Wallet not found with ID: INVALID_WALLET", exception.getMessage());
    }

    @Test
    void getWalletDetails_success() {
        String walletId = "WALB7A0E0285";

        Wallet wallet = new Wallet();
        wallet.setWalletId(walletId);
        wallet.setBalance(new BigDecimal("1000"));
        wallet.setBuyer(buyer);

        when(walletRepository.findByWalletId(walletId)).thenReturn(Optional.of(wallet));

        Wallet result = walletService.getWalletDetails(walletId);

        assertNotNull(result);
        assertEquals(walletId, result.getWalletId());
        assertEquals(new BigDecimal("1000"), result.getBalance());
        assertEquals(buyer, result.getBuyer());
    }
    @Test
    void getWalletDetails_walletNotFound() {
        String walletId = "INVALID_WALLET";

        when(walletRepository.findByWalletId(walletId)).thenReturn(Optional.empty());

        WalletNotFoundException exception = assertThrows(WalletNotFoundException.class, () -> {
            walletService.getWalletDetails(walletId);
        });

        assertEquals("Wallet not found with ID: " + walletId, exception.getMessage());
    }


    // to gel walletList details test cases
    @Test
    void testGetWalletListByBuyer_Success() {
        when(buyerRepository.findByEmailId("bhooli@gmail.com")).thenReturn(Optional.of(buyer));
        when(walletRepository.findAllByBuyer(buyer)).thenReturn(Collections.singletonList(wallet));

        List<Wallet> result = walletService.getWalletListByBuyer("bhooli@gmail.com");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("WAL123", result.get(0).getWalletId());

        verify(buyerRepository, times(1)).findByEmailId("bhooli@gmail.com");
        verify(walletRepository, times(1)).findAllByBuyer(buyer);
    }

    @Test
    void testGetWalletListByBuyer_EmptyList() {
        when(buyerRepository.findByEmailId("bhooli@gmail.com")).thenReturn(Optional.of(buyer));
        when(walletRepository.findAllByBuyer(buyer)).thenReturn(Collections.emptyList());

        assertThrows(WalletNotFoundException.class, () ->
                walletService.getWalletListByBuyer("bhooli@gmail.com"));

        verify(buyerRepository, times(1)).findByEmailId("bhooli@gmail.com");
        verify(walletRepository, times(1)).findAllByBuyer(buyer);
    }

    @Test
    void testGetWalletListByBuyer_BuyerNotFound() {
        when(buyerRepository.findByEmailId("unknown@gmail.com")).thenReturn(Optional.empty());

        assertThrows(BuyerNotFoundException.class, () ->
                walletService.getWalletListByBuyer("unknown@gmail.com"));

        verify(buyerRepository, times(1)).findByEmailId("unknown@gmail.com");
        verify(walletRepository, never()).findAllByBuyer(any());
    }

    //  Test: Successfully set a new primary wallet
    @Test
    void testSetPrimaryWallet_Success() {
        when(walletRepository.findByBuyerEmailId(buyer.getEmailId()))
                .thenReturn(Arrays.asList(wallet1, wallet2));

        when(walletRepository.findById("WAL456"))
                .thenReturn(Optional.of(wallet2));

        List<Wallet> wallets = Arrays.asList(wallet1, wallet2);
        walletService.setPrimaryWallet("WAL456", buyer.getEmailId());

        assertFalse(wallet1.getPrimary(), "Old primary should be false");
        assertTrue(wallet2.getPrimary(), "New primary should be true");

        verify(walletRepository, times(1)).saveAll(anyList());
        verify(walletRepository, times(1)).saveAll(wallets);
    }

    // Test: Wallet ID not found
    @Test
    void testSetPrimaryWallet_WalletNotFound() {
        when(walletRepository.findByBuyerEmailId(buyer.getEmailId()))
                .thenReturn(List.of(wallet1, wallet2));
        when(walletRepository.findById("INVALID"))
                .thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class,
                () -> walletService.setPrimaryWallet("INVALID", buyer.getEmailId()));
    }

    // Test: Successfully get primary wallet
    @Test
    void testGetPrimaryWallet_Success() {
        when(walletRepository.findByBuyerEmailIdAndIsPrimaryTrue(buyer.getEmailId()))
                .thenReturn(Optional.of(wallet1));

        Wallet result = walletService.getPrimaryWallet(buyer.getEmailId());

        assertNotNull(result);
        assertEquals(wallet1.getWalletId(), result.getWalletId());
        verify(walletRepository, times(1))
                .findByBuyerEmailIdAndIsPrimaryTrue(buyer.getEmailId());
    }

    //  Test: No primary wallet set
    @Test
    void testGetPrimaryWallet_NotFound() {
        when(walletRepository.findByBuyerEmailIdAndIsPrimaryTrue(buyer.getEmailId()))
                .thenReturn(Optional.empty());

        assertThrows(PrimaryWalletNotfound.class,
                () -> walletService.getPrimaryWallet(buyer.getEmailId()));
    }

}