package com.batua.service;

import com.batuaa.transactionservice.exception.EmptyTransactionListException;
import com.batuaa.transactionservice.model.Buyer;
import com.batuaa.transactionservice.model.Status;
import com.batuaa.transactionservice.model.Transaction;
import com.batuaa.transactionservice.model.Wallet;
import com.batuaa.transactionservice.repository.TransactionRepository;
import com.batuaa.transactionservice.repository.WalletRepository;
import com.batuaa.transactionservice.service.TranscationSerivceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {


    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TranscationSerivceImp transactionService;

    private Wallet walletFrom;
    private Buyer fromBuyer;
    private Wallet walletTo;
    private Transaction transaction;
    private List<Transaction> transactionList;

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

        fromBuyer = new Buyer();
        fromBuyer.setEmailId("sakshi@gmail.com");

        transaction = new Transaction();
        transaction.setTransactionId(1);
        transaction.setFromWallet(walletFrom);
        transaction.setToWallet(walletTo);
        transaction.setAmount(new BigDecimal("200"));
        transaction.setStatus(Status.SUCCESS);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setRemarks("Transferred Rs200 to wallet wallet456");

        transactionList = new ArrayList<>();
        transactionList.add(transaction);

    }

    @Test
    public void givenGetAllTransactionsForWalletIdThenShouldReturnListOfAllTransactionsForWalletId()
            throws EmptyTransactionListException {

        transactionRepository.save(transaction);
        when(transactionRepository.findByEmailAndWallet("sakshi@gmail.com","WALLET001")).thenReturn(transactionList);
        List<Transaction> transactionList1 = transactionService.getAllTransactions("sakshi@gmail.com","WALLET001");
        assertEquals(transactionList,transactionList1);
        verify(transactionRepository,times(1)).save(transaction);
        verify(transactionRepository, times(1)).findByEmailAndWallet("sakshi@gmail.com","WALLET001");

    }

}
