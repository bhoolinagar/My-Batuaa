package com.myBatuaa.service;

import com.myBatuaa.exception.*;
import com.myBatuaa.model.BankAccount;
import com.myBatuaa.repository.BankAccountResposity;
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
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


/* @SpringBootTest(classes = TransactionServiceApplicationTests.class) | once integrated will modify as needed commenting because
methods required to be tested using Mocked data only, SpringBootTest injecting actual entities hence commented */
class TransactionServiceTests {

    @Mock
    private BankAccountResposity bankAccountResposity;
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
        walletFrom.setCreatedAt(LocalDateTime.now());
        walletTo = new Wallet();
        walletTo.setWalletId("WALLET002");
        walletTo.setBalance(BigDecimal.valueOf(3000));
        walletTo.setCreatedAt(LocalDateTime.now());
    }
    @Test
    void testAddMoney_amountNull() {

        AmountCanNotBeNullException ex = assertThrows(AmountCanNotBeNullException.class,
                () -> transactionService.addMoney("WALLET001", "12345", null));

        assertEquals("Amount Should Not Be Null", ex.getMessage());

        verify(bankAccountResposity, never()).findById(any());
        verify(walletRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }
    @Test
    void testAddMoney_walletNotFound() {

        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber("12345");
        bankAccount.setWallet(null); // no linked wallet initially for the test check

        when(bankAccountResposity.findById("12345")).thenReturn(Optional.of(bankAccount));

        WalletNotFoundException ex = assertThrows(WalletNotFoundException.class, () -> transactionService.addMoney("WALLET999", "12345", BigDecimal.valueOf(1000)));

        assertEquals("Wallet not linked to bank account: 12345", ex.getMessage());

        verify(bankAccountResposity).findById("12345");
        verify(walletRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void testAddMoney_success() {

        Wallet wallet = new Wallet("WALLET001", null, null, BigDecimal.valueOf(5000), null, null, null);

        BankAccount bankAccount = new BankAccount("12345", 0, null, null, BigDecimal.valueOf(10000), wallet);

        when(bankAccountResposity.findById("12345")).thenReturn(Optional.of(bankAccount));
        when(bankAccountResposity.save(any())).thenReturn(bankAccount);
        when(walletRepository.save(any())).thenReturn(wallet);
        when(transactionRepository.save(any())).thenAnswer(i -> {
            Transaction t = i.getArgument(0);
            t.setStatus(Status.SUCCESS);
            t.setRemarks("Money added to wallet WALLET001");
            return t;
        });

        Transaction result = transactionService.addMoney("WALLET001", "12345", BigDecimal.valueOf(1000));

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(1000), result.getAmount());
        assertEquals(Status.SUCCESS, result.getStatus());
        assertTrue(result.getRemarks().contains("Money added to wallet WALLET001"));

        verify(bankAccountResposity).findById("12345");
        verify(bankAccountResposity).save(any());
        verify(walletRepository).save(any());
        verify(transactionRepository).save(any());
    }
    @Test
    void testTransferWalletToWallet_amountNull() {

        AmountCanNotBeNullException ex = assertThrows(AmountCanNotBeNullException.class, () -> transactionService.transferWalletToWallet("WALLET001", "WALLET002", null));

        assertEquals("Amount Should Not Be Null", ex.getMessage());

        verify(walletRepository, never()).findByWalletId(any());
        verify(walletRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void testTransferWalletToWallet_senderNotFound() {

        when(walletRepository.findByWalletId("WALLET999")).thenReturn(Optional.empty());

        WalletNotFoundException exception = assertThrows(WalletNotFoundException.class, () ->
                transactionService.transferWalletToWallet("WALLET999", "WALLET002", BigDecimal.valueOf(1000)));

        assertEquals("Sender wallet not found: WALLET999", exception.getMessage());

        verify(walletRepository).findByWalletId("WALLET999");
        verify(walletRepository, never()).save(any(Wallet.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
    @Test
    void testTransferWalletToWallet_receiverNotFound() {

        when(walletRepository.findByWalletId("WALLET001")).thenReturn(Optional.of(walletFrom));
        when(walletRepository.findByWalletId("WALLET999")).thenReturn(Optional.empty());

        WalletNotFoundException exception = assertThrows(WalletNotFoundException.class, () -> transactionService.transferWalletToWallet("WALLET001", "WALLET999", BigDecimal.valueOf(1000)));

        assertEquals("Receiver wallet not foundWALLET999", exception.getMessage());

        verify(walletRepository).findByWalletId("WALLET001");
        verify(walletRepository).findByWalletId("WALLET999");
        verify(walletRepository, never()).save(any(Wallet.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
    @Test
    void testTransferWalletToWallet_insufficientFunds() {
        BigDecimal amount = BigDecimal.valueOf(6000);

        when(walletRepository.findByWalletId("WALLET001")).thenReturn(Optional.of(walletFrom));
        when(walletRepository.findByWalletId("WALLET002")).thenReturn(Optional.of(walletTo));

        InsufficientFundsException exception = assertThrows(InsufficientFundsException.class, () -> transactionService.transferWalletToWallet("WALLET001", "WALLET002", amount));

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
        assertEquals(walletFrom, resultSender.getFromWallet());
        assertEquals(walletTo, resultSender.getToWallet());
        assertEquals(BigDecimal.valueOf(4000), walletFrom.getBalance());
        assertEquals(BigDecimal.valueOf(4000), walletTo.getBalance());

        verify(walletRepository).findByWalletId("WALLET001");
        verify(walletRepository).findByWalletId("WALLET002");
        verify(walletRepository, times(2)).save(any(Wallet.class));
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }

    @Test
    void testFilterTransactionsByRemark_success(){
        String walletId = "WALLET001";
        String remark = "transferred";
        Transaction testTransaction = new Transaction();
        testTransaction.setRemarks("Transferred");
        testTransaction.setFromWallet(walletFrom);
        testTransaction.setToWallet(walletTo);
        when(walletRepository.findByWalletId(walletId)).thenReturn(Optional.of(walletFrom));
        when(transactionRepository.findByWalletAndRemarkContainingIgnoreCase(walletId,remark))
                .thenReturn(List.of(testTransaction));
        List<Transaction> result = transactionService.filterTransactionsByRemark(walletId,remark);
        assertNotNull(result);
        assertEquals(1,result.size());
        assertTrue(result.get(0).getRemarks().toLowerCase().contains(remark.toLowerCase()));

        verify(walletRepository).findByWalletId(walletId);
        verify(transactionRepository).findByWalletAndRemarkContainingIgnoreCase(walletId,remark);
    }

    @Test
    void testFilterTransactionsByRemark_noTransactionsFound() {

        String walletId = "WALLET001";
        String remark = "nonexistent";

        when(walletRepository.findByWalletId(walletId)).thenReturn(Optional.of(walletFrom));
        when(transactionRepository.findByWalletAndRemarkContainingIgnoreCase(walletId, remark))
                .thenReturn(List.of());


        ErrorOccurredUnableToFilterByRemarkException ex = assertThrows(ErrorOccurredUnableToFilterByRemarkException.class,
                () -> transactionService.filterTransactionsByRemark(walletId, remark)
        );
        assertEquals("Error occurred while filtering by remarks", ex.getMessage());

        verify(walletRepository).findByWalletId(walletId);
        verify(transactionRepository).findByWalletAndRemarkContainingIgnoreCase(walletId, remark);
    }

    @Test
    void testFilterTransactionsByRemark_emptyRemark_returnsAllTransactions() {
        String walletId = "WALLET001";
        String remark = "";
        Transaction testTransaction = new Transaction();
        testTransaction.setRemarks("Payment received");
        when(walletRepository.findByWalletId(walletId)).thenReturn(Optional.of(walletFrom));
        when(transactionRepository.findByWalletAndRemarkContainingIgnoreCase(walletId, ""))
                .thenReturn(List.of(testTransaction));
        List<Transaction> result = transactionService.filterTransactionsByRemark(walletId, remark);
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(walletRepository).findByWalletId(walletId);
        verify(transactionRepository).findByWalletAndRemarkContainingIgnoreCase(walletId, "");
    }
    @Test
    void testFilterTransactionsByRemark_walletIdNullOrBlank() {
        String walletId = "";
        String remark = "payment";

        WalletNotFoundException ex = assertThrows(WalletNotFoundException.class, () -> transactionService.filterTransactionsByRemark(walletId, remark));

        assertEquals("Wallet Not Found or is invalid", ex.getMessage());

        verify(walletRepository, never()).findByWalletId(any());
        verify(transactionRepository, never()).findByWalletAndRemarkContainingIgnoreCase(any(),any());
    }


}
