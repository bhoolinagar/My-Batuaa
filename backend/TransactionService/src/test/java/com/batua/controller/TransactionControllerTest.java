package com.batua.controller;

import com.batuaa.transactionservice.controller.TransactionController;
import com.batuaa.transactionservice.exception.EmptyTransactionListException;
import com.batuaa.transactionservice.model.Buyer;
import com.batuaa.transactionservice.model.Status;
import com.batuaa.transactionservice.model.Transaction;
import com.batuaa.transactionservice.model.Wallet;
import com.batuaa.transactionservice.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private Wallet walletFrom;
    private Wallet walletTo;
    private Buyer fromBuyer;
    private Transaction senderTransaction;
    List<Transaction> transactionList;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();

        fromBuyer = new Buyer();
        fromBuyer.setEmailId("sakshi@gmail.com");

        walletFrom = new Wallet();
        walletFrom.setWalletId("wallet123");
        walletFrom.setBalance(new BigDecimal("1000"));

        walletTo = new Wallet();
        walletTo.setWalletId("wallet456");
        walletTo.setBalance(new BigDecimal("500"));

        // Sample sender transaction returned by service
        senderTransaction = new Transaction();
        senderTransaction.setTransactionId(1);
        senderTransaction.setFromWallet(walletFrom);
        senderTransaction.setToWallet(walletTo);
        senderTransaction.setAmount(new BigDecimal("200"));
        senderTransaction.setStatus(Status.SUCCESS);
        senderTransaction.setTimestamp(LocalDateTime.now());
        senderTransaction.setRemarks("Transferred Rs200 to wallet wallet456");
        senderTransaction.setFromBuyer(fromBuyer);

        transactionList = new ArrayList<>();
        transactionList.add(senderTransaction);
    }

    @Test
    public void givenGetAllTransactionsForWalletIdThenShouldReturnListOfAllTransactionsForWalletId()  {


        when(transactionService.getAllTransactions("sakshi@gmail.com","wallet123")).thenReturn(transactionList);
        ResponseEntity<?> response = transactionController.getAllTransactions("sakshi@gmail.com","wallet123");
        ;
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transactionList, response.getBody());
        verify(transactionService, times(1)).getAllTransactions("sakshi@gmail.com","wallet123");


    }



}
