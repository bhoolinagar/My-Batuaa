package com.myBatuaa.controller;

import com.myBatuaa.exception.InsufficientFundsException;
import com.myBatuaa.exception.WalletNotFoundException;
import com.myBatuaa.service.TransactionService;
import org.junit.jupiter.api.Test;

import com.myBatuaa.model.Transaction;
import com.myBatuaa.model.*;
import com.myBatuaa.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;



class TransactionControllerTest {
    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private Wallet walletFrom;
    private Wallet walletTo;
    private Transaction senderTransaction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        walletFrom = new Wallet();
        walletFrom.setWalletId("wallet123");
        walletFrom.setBalance(new BigDecimal("1000"));

        walletTo = new Wallet();
        walletTo.setWalletId("wallet456");
        walletTo.setBalance(new BigDecimal("500"));

        // Sample sender transaction returned by service
        senderTransaction = new Transaction();
        senderTransaction.setFromWallet(walletFrom);
        senderTransaction.setToWallet(walletTo);
        senderTransaction.setAmount(new BigDecimal("200"));
        senderTransaction.setStatus(Status.SUCCESS);
        senderTransaction.setTimestamp(LocalDateTime.now());
        senderTransaction.setRemarks("Transferred Rs200 to wallet wallet456");
    }
    @Test
    void testAddMoneyFromBank_WalletNotFound() {
        when(transactionService.addMoney("invalidWallet", new BigDecimal("500")))
                .thenThrow(new WalletNotFoundException("Wallet not found"));
        WalletNotFoundException exception = assertThrows(WalletNotFoundException.class, () ->
                transactionController.addMoneyFromBank("invalidWallet", "123456", new BigDecimal("500"))
        );
        assertEquals("Wallet not found", exception.getMessage());
        verify(transactionService, times(1)).addMoney("invalidWallet", new BigDecimal("500"));
    }
    @Test
    void testAddMoneyFromBank_NegativeAmount() {
        when(transactionService.addMoney("wallet123", new BigDecimal("-100")))
                .thenThrow(new IllegalArgumentException("Check the amount entered"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                transactionController.addMoneyFromBank("wallet123", "123456", new BigDecimal("-100"))
        );
        assertEquals("Check the amount entered", exception.getMessage());
        verify(transactionService, times(1)).addMoney("wallet123", new BigDecimal("-100"));
    }
    @Test
    void testAddMoneyFromBank_Success() {
        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal("1000"));
        transaction.setStatus(Status.SUCCESS);
        transaction.setTimestamp(LocalDateTime.now());

        when(transactionService.addMoney("wallet123", new BigDecimal("1000")))
                .thenReturn(transaction);
        ResponseEntity<?> response = transactionController.addMoneyFromBank("wallet123", "123456", new BigDecimal("1000"));
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(transaction, response.getBody());
        verify(transactionService, times(1)).addMoney("wallet123", new BigDecimal("1000"));
    }

    @Test
    void testTransferMoneyWalletToWallet_InsufficientFunds() {
        when(transactionService.transferWalletToWallet("wallet123", "wallet456", new BigDecimal("5000")))
                .thenThrow(new InsufficientFundsException("Insufficient funds"));
        InsufficientFundsException exception = assertThrows(InsufficientFundsException.class, () ->
                transactionController.transferMoneywalletToWallet("wallet123", "wallet456", new BigDecimal("5000"))
        );
        assertEquals("Insufficient funds", exception.getMessage());
        verify(transactionService, times(1)).transferWalletToWallet("wallet123", "wallet456", new BigDecimal("5000"));
    }
    @Test
    void testTransferMoneyWalletToWallet_WalletNotFound() {
        when(transactionService.transferWalletToWallet( "invalidWallet", "wallet456", new BigDecimal("100")))
                .thenThrow(new WalletNotFoundException("Sender wallet not found: invalidWallet"));
        WalletNotFoundException exception = assertThrows(WalletNotFoundException.class, () -> transactionController.transferMoneywalletToWallet("invalidWallet", "wallet456", new BigDecimal("100"))
        );
        assertEquals("Sender wallet not found: invalidWallet", exception.getMessage()); verify(transactionService, times(1))
                .transferWalletToWallet("invalidWallet", "wallet456", new BigDecimal("100")); }
    @Test
    void testTransferMoneyWalletToWallet_Success() {
        when(transactionService.transferWalletToWallet("wallet123", "wallet456", new BigDecimal("200")))
                .thenReturn(senderTransaction);
        ResponseEntity<?> response = transactionController.transferMoneywalletToWallet("wallet123", "wallet456", new BigDecimal("200")
        );
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(senderTransaction, response.getBody());
        verify(transactionService, times(1)).transferWalletToWallet("wallet123", "wallet456", new BigDecimal("200"));
    }
}