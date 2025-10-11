package com.myBatuaa.service;

import com.myBatuaa.exception.InsufficientFundsException;
import com.myBatuaa.exception.WalletNotFoundException;
import org.junit.jupiter.api.Test;

import com.myBatuaa.model.Wallet;
import com.myBatuaa.model.Transaction;
import com.myBatuaa.model.Status;
import com.myBatuaa.repository.WalletRepository;
import com.myBatuaa.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


/* @SpringBootTest(classes = TransactionServiceApplicationTests.class) | once integrated will modify as needed commenting because
methods required to be tested using Mocked data only, SpringBootTest injecting actual entities hence commented */
class TransactionServiceTests {


    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImp transactionService;

    private Wallet walletFrom;
    private Wallet walletTo;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        walletFrom = new Wallet();
        walletFrom.setWalletId("WALLET001");
        walletFrom.setBalance(BigDecimal.valueOf(5000));
        walletFrom.setCreatedAt(LocalDate.now());

        walletTo = new Wallet();
        walletTo.setWalletId("WALLET002");
        walletTo.setBalance(BigDecimal.valueOf(3000));
        walletTo.setCreatedAt(LocalDate.now());
    }
    @Test
    void testAddMoney_walletNotFound() {
        when(walletRepository.findByWalletId("WALLET999")).thenReturn(Optional.empty());

        WalletNotFoundException exception = assertThrows(WalletNotFoundException.class, () ->
                transactionService.addMoney("WALLET999", BigDecimal.valueOf(1000))
        );
        assertEquals("Wallet not found.", exception.getMessage());
        verify(walletRepository).findByWalletId("WALLET999");
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
    @Test
    void testAddMoney_success() {

        when(walletRepository.findByWalletId("WALLET001")).thenReturn(Optional.of(walletFrom));

        Transaction savedTransaction = new Transaction();
        savedTransaction.setAmount(BigDecimal.valueOf(1000));
        savedTransaction.setStatus(Status.SUCCESS);
        savedTransaction.setRemarks("Money added to wallet WALLET001");
        savedTransaction.setTimestamp(LocalDateTime.now());

        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        Transaction result = transactionService.addMoney("WALLET001", BigDecimal.valueOf(1000));

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(1000), result.getAmount());
        assertEquals(Status.SUCCESS, result.getStatus());
        assertTrue(result.getRemarks().contains("Money added to wallet WALLET001"));
        verify(walletRepository).findByWalletId("WALLET001");
        verify(transactionRepository).save(any(Transaction.class));
    }
    @Test
    void testTransferWalletToWallet_senderNotFound() {
        when(walletRepository.findByWalletId("WALLET999")).thenReturn(Optional.empty());
        WalletNotFoundException exception = assertThrows(WalletNotFoundException.class, () ->
                transactionService.transferWalletToWallet("WALLET999", "WALLET002", BigDecimal.valueOf(1000))
        );
        assertEquals("Sender wallet not found: WALLET999", exception.getMessage());
        verify(walletRepository).findByWalletId("WALLET999");
        verify(walletRepository, never()).save(any(Wallet.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
    @Test
    void testTransferWalletToWallet_recipientNotFound() {
        when(walletRepository.findByWalletId("WALLET001")).thenReturn(Optional.of(walletFrom));
        when(walletRepository.findByWalletId("WALLET999")).thenReturn(Optional.empty());
        WalletNotFoundException exception = assertThrows(WalletNotFoundException.class, () -> transactionService.transferWalletToWallet("WALLET001", "WALLET999", BigDecimal.valueOf(1000)));
        assertEquals("Recipient wallet not foundWALLET999", exception.getMessage());
        verify(walletRepository).findByWalletId("WALLET001");
        verify(walletRepository).findByWalletId("WALLET999");
        verify(walletRepository, never()).save(any(Wallet.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
    @Test
    void testTransferWalletToWallet_insufficientFunds() {
        BigDecimal amount = BigDecimal.valueOf(6000); // more than walletFrom balance
        when(walletRepository.findByWalletId("WALLET001")).thenReturn(Optional.of(walletFrom));
        when(walletRepository.findByWalletId("WALLET002")).thenReturn(Optional.of(walletTo));
        InsufficientFundsException exception = assertThrows(InsufficientFundsException.class, () ->
                transactionService.transferWalletToWallet("WALLET001", "WALLET002", amount)
        );
        assertEquals("Insufficient funds", exception.getMessage());
        verify(walletRepository).findByWalletId("WALLET001");
        verify(walletRepository).findByWalletId("WALLET002");
        verify(walletRepository, never()).save(any(Wallet.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
    @Test
    void testTransferWalletToWallet_success() {
        BigDecimal amount = BigDecimal.valueOf(1000);
        when(walletRepository.findByWalletId("WALLET001")).thenReturn(Optional.of(walletFrom));
        when(walletRepository.findByWalletId("WALLET002")).thenReturn(Optional.of(walletTo));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(i -> i.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));
        Transaction resultSender = transactionService.transferWalletToWallet("WALLET001", "WALLET002", amount);

        assertNotNull(resultSender);
        assertEquals(BigDecimal.valueOf(1000), resultSender.getAmount());
        assertEquals(Status.SUCCESS, resultSender.getStatus());
        assertTrue(resultSender.getRemarks().contains("WALLET002"));

        assertEquals(BigDecimal.valueOf(4000), walletFrom.getBalance());
        assertEquals(BigDecimal.valueOf(4000), walletTo.getBalance());

        verify(walletRepository).findByWalletId("WALLET001");
        verify(walletRepository).findByWalletId("WALLET002");
        verify(walletRepository, times(2)).save(any(Wallet.class));
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }

}
