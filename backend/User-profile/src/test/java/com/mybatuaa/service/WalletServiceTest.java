package com.mybatuaa.service;

import com.mybatuaa.exception.BankAccountAlreadyLinkedException;
import com.mybatuaa.exception.WalletNotFoundException;
import com.mybatuaa.model.BankAccount;
import com.mybatuaa.model.Wallet;
import com.mybatuaa.repository.BankAccountRepository;
import com.mybatuaa.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.AssertionsKt.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

//@SpringBootTest
public class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;
    @InjectMocks
    private  WalletServiceImpl walletService;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @InjectMocks
    private BankServiceImpl bankService;

    private Wallet wallet;
    private BankAccount bankAccount;

    @BeforeEach
    void  SetUp(){

        MockitoAnnotations.openMocks(this);
        wallet= new Wallet();
        wallet.setWalletId("WAL012");
        wallet.setBalance(new BigDecimal(900));
        bankAccount = new BankAccount();
        bankAccount.setAccountNumber("1234567890");

    }


    @Test
    void testLinkBankAccountToWallet_Success() {
        when(walletRepository.findByWalletId("wallet123")).thenReturn(Optional.of(wallet));
        when(bankAccountRepository.existsByAccountNumberAndWallet_WalletId("1234567890", "wallet123")).thenReturn(false);
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(bankAccount);

        BankAccount linkedAccount = bankService.linkBankAccountToWallet("wallet123", bankAccount);

        assertNotNull(linkedAccount);
        assertEquals("1234567890", linkedAccount.getAccountNumber());
        verify(walletRepository, times(1)).findByWalletId("wallet123");
        verify(bankAccountRepository, times(1)).save(bankAccount);
    }

    @Test
    void testLinkBankAccountToWallet_WalletNotFound() {
        when(walletRepository.findByWalletId("invalidWallet")).thenReturn(Optional.empty());

        WalletNotFoundException exception = assertThrows(WalletNotFoundException.class,
                () -> bankService.linkBankAccountToWallet("invalidWallet", bankAccount));

        assertEquals("Wallet not found with ID: invalidWallet", exception.getMessage());
    }

    @Test
    void testLinkBankAccountToWallet_AlreadyLinked() {
        when(walletRepository.findByWalletId("wallet123")).thenReturn(Optional.of(wallet));
        when(bankAccountRepository.existsByAccountNumberAndWallet_WalletId("1234567890", "wallet123")).thenReturn(true);

        BankAccountAlreadyLinkedException exception = assertThrows(BankAccountAlreadyLinkedException.class,
                () -> bankService.linkBankAccountToWallet("wallet123", bankAccount));

        assertEquals("Bank account 1234567890 already linked to this wallet", exception.getMessage());
    }

    @Test
    void  testGetWalletBalance_Success(){
        when(walletRepository.findByWalletId("WAL012")).thenReturn(Optional.of(wallet));
        BigDecimal balance= walletService.getWalletBalance(wallet.getWalletId());
        assertEquals(new BigDecimal(900),balance);
        verify(walletRepository, times(1)).findByWalletId("WAL012");


    }

    @Test
    void testGetWalletBalanceWalletNotFound(){
        when(walletRepository.findByWalletId("invalidWallet")).thenReturn(Optional.empty());
        WalletNotFoundException exception= assertThrows(WalletNotFoundException.class,()->walletService.getWalletBalance("invalidWallet"));

        assertEquals("Wallet not found with ID: "+"invalidWallet",exception.getMessage());
        verify(walletRepository,times(1)).findByWalletId("invalidWallet");
    }

}
