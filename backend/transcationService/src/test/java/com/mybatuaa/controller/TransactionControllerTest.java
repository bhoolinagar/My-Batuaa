package com.mybatuaa.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.mybatuaa.exception.AmountCanNotBeNullException;
import com.mybatuaa.exception.EmptyTransactionListException;
import com.mybatuaa.exception.ErrorOccurredUnableToFilterByRemarkException;
import com.mybatuaa.exception.InsufficientFundsException;
import com.mybatuaa.exception.TransactionNotFoundException;
import com.mybatuaa.exception.WalletNotFoundException;
import com.mybatuaa.model.Status;
import com.mybatuaa.model.Transaction;
import com.mybatuaa.model.Wallet;
import com.mybatuaa.service.TransactionService;

class TransactionControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Mock
	private TransactionService transactionService;

	@InjectMocks
	private TransactionController transactionController;

	private Wallet walletFrom;
	private Wallet walletTo;
	private Transaction senderTransaction;

	// for translations
	private Transaction txn1;
	private Transaction txn2;
    List<Transaction> transactionList;
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
		walletFrom = new Wallet();
		walletFrom.setWalletId("wallet123");
		walletFrom.setBalance(new BigDecimal("1000"));
		walletTo = new Wallet();
		walletTo.setWalletId("wallet456");
		walletTo.setBalance(new BigDecimal("500"));

		senderTransaction = new Transaction();
		senderTransaction.setTransactionId(3);
		senderTransaction.setFromWallet(walletFrom);
		senderTransaction.setToWallet(walletTo);
		senderTransaction.setAmount(new BigDecimal("200"));
		senderTransaction.setStatus(Status.SUCCESS);
		senderTransaction.setTimestamp(LocalDateTime.now());
		senderTransaction.setRemarks("Transferred Rs200 to wallet wallet456");

		// transactions
		txn1 = new Transaction();
		txn1.setFromWallet(walletFrom);
		txn1.setToWallet(walletTo);
		txn1.setStatus(Status.SUCCESS);
		txn1.setTransactionId(1);
		txn1.setAmount(BigDecimal.valueOf(1000));
		txn1.setTimestamp(LocalDateTime.of(2025, 10, 10, 10, 0));
		txn2 = new Transaction();
		txn2.setTransactionId(2);
		txn2.setFromWallet(walletFrom);
		txn2.setToWallet(walletTo);
		txn2.setStatus(Status.SUCCESS);
		txn2.setAmount(BigDecimal.valueOf(500));
		txn2.setTimestamp(LocalDateTime.of(2025, 10, 9, 9, 0));

        transactionList = new ArrayList<>();
        transactionList.add(senderTransaction);
    }

	@Test
	void testAddMoneyFromBank_AmountNull() {
		when(transactionService.addMoney("wallet123", "123456", null))
		.thenThrow(new AmountCanNotBeNullException("Amount Should Not Be Null"));

		AmountCanNotBeNullException exception = assertThrows(AmountCanNotBeNullException.class,
				() -> transactionController.addMoneyFromBank("wallet123", "123456", null));
		assertEquals("Amount Should Not Be Null", exception.getMessage());
		verify(transactionService, times(1)).addMoney("wallet123", "123456", null);
	}

	@Test
	void testAddMoneyFromBank_WalletNotFound() {
		when(transactionService.addMoney("invalidWallet", "212121", new BigDecimal("500")))
		.thenThrow(new WalletNotFoundException("Wallet not found"));
		WalletNotFoundException exception = assertThrows(WalletNotFoundException.class,
				() -> transactionController.addMoneyFromBank("invalidWallet", "212121", new BigDecimal("500")));
		assertEquals("Wallet not found", exception.getMessage());
		verify(transactionService, times(1)).addMoney("invalidWallet", "212121", new BigDecimal("500"));
	}

	@Test
	void testAddMoneyFromBank_NegativeAmount() {
		when(transactionService.addMoney("wallet123", "123456", new BigDecimal("-100")))
		.thenThrow(new IllegalArgumentException("Check the amount entered"));

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> transactionController.addMoneyFromBank("wallet123", "123456", new BigDecimal("-100")));
		assertEquals("Check the amount entered", exception.getMessage());
		verify(transactionService, times(1)).addMoney("wallet123", "123456", new BigDecimal("-100"));
	}

	@Test
	void testAddMoneyFromBank_Success() {
		Transaction transaction = new Transaction();
		transaction.setAmount(new BigDecimal("1000"));
		transaction.setStatus(Status.SUCCESS);
		transaction.setTimestamp(LocalDateTime.now());

		when(transactionService.addMoney("wallet123", "123456", new BigDecimal("1000"))).thenReturn(transaction);
		ResponseEntity<?> response = transactionController.addMoneyFromBank("wallet123", "123456",
				new BigDecimal("1000"));
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals(transaction, response.getBody());
		verify(transactionService, times(1)).addMoney("wallet123", "123456", new BigDecimal("1000"));
	}

	@Test
	void testTransferMoneyWalletToWallet_AmountNull() {
		when(transactionService.transferWalletToWallet("wallet123", "wallet456", null))
		.thenThrow(new AmountCanNotBeNullException("Amount Should Not Be Null"));
		AmountCanNotBeNullException exception = assertThrows(AmountCanNotBeNullException.class,
				() -> transactionController.transferMoneywalletToWallet("wallet123", "wallet456", null));
		assertEquals("Amount Should Not Be Null", exception.getMessage());
		verify(transactionService, times(1)).transferWalletToWallet("wallet123", "wallet456", null);
	}

	@Test
	void testTransferMoneyWalletToWallet_InsufficientFunds() {
		when(transactionService.transferWalletToWallet("wallet123", "wallet456", new BigDecimal("5000")))
		.thenThrow(new InsufficientFundsException("Insufficient funds"));
		InsufficientFundsException exception = assertThrows(InsufficientFundsException.class,
				() -> transactionController.transferMoneywalletToWallet("wallet123", "wallet456",
						new BigDecimal("5000")));
		assertEquals("Insufficient funds", exception.getMessage());
		verify(transactionService, times(1)).transferWalletToWallet("wallet123", "wallet456", new BigDecimal("5000"));
	}

	@Test
	void testTransferMoneyWalletToWallet_WalletNotFound() {
		when(transactionService.transferWalletToWallet("invalidWallet", "wallet456", new BigDecimal("100")))
		.thenThrow(new WalletNotFoundException("Sender wallet not found: invalidWallet"));
		WalletNotFoundException exception = assertThrows(WalletNotFoundException.class, () -> transactionController
				.transferMoneywalletToWallet("invalidWallet", "wallet456", new BigDecimal("100")));
		assertEquals("Sender wallet not found: invalidWallet", exception.getMessage());
		verify(transactionService, times(1)).transferWalletToWallet("invalidWallet", "wallet456",
				new BigDecimal("100"));
	}

	@Test
	void testTransferMoneyWalletToWallet_Success() {
		when(transactionService.transferWalletToWallet("wallet123", "wallet456", new BigDecimal("200")))
		.thenReturn(senderTransaction);
		ResponseEntity<?> response = transactionController.transferMoneywalletToWallet("wallet123", "wallet456",
				new BigDecimal("200"));
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals(senderTransaction, response.getBody());
		verify(transactionService, times(1)).transferWalletToWallet("wallet123", "wallet456", new BigDecimal("200"));
	}

	@Test
	void testViewTransactionByRemark_NoTransactionsFound() {
		String walletId = "wallet123", remark = "nonexistent";
		when(transactionService.filterTransactionsByRemark(walletId, remark))
		.thenThrow(new TransactionNotFoundException("No transaction found"));

		var ex = assertThrows(TransactionNotFoundException.class,
				() -> transactionController.viewTransactionByRemark(walletId, remark));

		assertEquals("No transaction found", ex.getMessage());
	}

	@Test
	void testViewTransactionByRemark_UnableToFilterError() {
		String walletId = "wallet123", remark = "refund";
		when(transactionService.filterTransactionsByRemark(walletId, remark))
		.thenThrow(new ErrorOccurredUnableToFilterByRemarkException("Error filtering"));

		var ex = assertThrows(ErrorOccurredUnableToFilterByRemarkException.class,
				() -> transactionController.viewTransactionByRemark(walletId, remark));

		assertEquals("Error filtering", ex.getMessage());
	}

	@Test
	void testViewTransactionByRemark_WalletNotFound() {
		String walletId = "inval", remark = "payment";
		when(transactionService.filterTransactionsByRemark(walletId, remark))
		.thenThrow(new WalletNotFoundException("Wallet Not Found or is invalid"));

		WalletNotFoundException exception = assertThrows(WalletNotFoundException.class,
				() -> transactionController.viewTransactionByRemark(walletId, remark));

		assertEquals("Wallet Not Found or is invalid", exception.getMessage());
	}

	@Test
	void testViewTransactionByRemark_Success() {
		String walletId = "wallet123", remark = "transfer";
		Transaction testTransaction = new Transaction();
		testTransaction.setRemarks("Transfer to");
		when(transactionService.filterTransactionsByRemark(walletId, remark)).thenReturn(List.of(testTransaction));
		ResponseEntity<?> response = transactionController.viewTransactionByRemark(walletId, remark);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(1, ((List<?>) response.getBody()).size());
	}

	// Test successful retrieval of translations
	@Test
	void testGetTransactionByWalletAndDate_success() throws Exception {
		List<Transaction> transactions = List.of(senderTransaction);
		// Stubbing
		Mockito.when(transactionService.filterTransactionsByDateRange(anyString(), any(LocalDate.class),
				any(LocalDate.class))).thenReturn(transactions);

		mockMvc.perform(get("/transaction/api/v1/view-transactions-by-date").param("walletId", "wallet123")
				.param("startDate", "2025-10-01").param("endDate", "2025-10-10")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
		.andExpect(jsonPath("$[0].amount").value(200)).andExpect(jsonPath("$[0].status").value("SUCCESS"));

	}

	// test when no translations are found
	@Test
	void testGetTranslationsByDate_Empty() throws Exception {

		// Stubbing
		Mockito.when(transactionService.filterTransactionsByDateRange(anyString(), any(LocalDate.class),
				any(LocalDate.class))).thenReturn(List.of());
		mockMvc.perform(get("/transaction/api/v1/view-transactions-by-date").param("walletId", "WAL2012")
				.param("startDate", "2025-10-01").param("endDate", "2025-10-10")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("$").isEmpty());

	}

	// test when service throws exception
	@Test
	void testGetTranscationsByDate_Failure() throws Exception {

		Mockito.when(transactionService.filterTransactionsByDateRange(anyString(), any(LocalDate.class),
				any(LocalDate.class))).thenThrow(new RuntimeException("Database error"));
		mockMvc.perform(get("/transaction/api/v1/view-transactions-by-date").param("walletId", "WAL2012")
				.param("startDate", "2025-10-01").param("endDate", "2025-10-10")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().is5xxServerError())
		.andExpect(content().string("Database error"));
	}

	// Successful response
	@Test
	void testViewTransactionByType_Success() throws Exception {
		List<Transaction> transactions = Arrays.asList(senderTransaction, txn1, txn2);

		when(transactionService.filterTransactionByType("wallet123", Status.SUCCESS)).thenReturn(transactions);

		mockMvc.perform(get("/transaction/api/v1/view-transactions-by-type").param("walletId", "wallet123")
				.param("type", "SUCCESS").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
		.andExpect(jsonPath("$[0].transactionId").value(3)).andExpect(jsonPath("$[1].transactionId").value(1))
		.andExpect(jsonPath("$[2].transactionId").value(2));

		verify(transactionService, times(1)).filterTransactionByType("wallet123", Status.SUCCESS);
	}

	// Empty transaction list → 404 Not Found
	@Test
	void testViewTransactionByType_EmptyList_NotFound() throws Exception {
		when(transactionService.filterTransactionByType("wallet123", Status.SUCCESS))
		.thenThrow(new EmptyTransactionListException("No transactions found"));

		mockMvc.perform(get("/transaction/api/v1/view-transactions-by-type").param("walletId", "wallet123")
				.param("type", "SUCCESS").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound())
		.andExpect(content().string("No transactions found"));

		verify(transactionService, times(1)).filterTransactionByType("wallet123", Status.SUCCESS);
	}

	// Invalid wallet ID → simulate generic exception → 500
	@Test
	void testViewTransactionByType_InternalServerError() throws Exception {
		when(transactionService.filterTransactionByType("invalidWallet", Status.FAILED))
		.thenThrow(new RuntimeException("Database failure"));

		mockMvc.perform(get("/transaction/api/v1/view-transactions-by-type").param("walletId", "invalidWallet")
				.param("type", "FAILED").contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isInternalServerError()).andExpect(content().string("Internal Server Error"));

		verify(transactionService, times(1)).filterTransactionByType("invalidWallet", Status.FAILED);
	}

	// Missing required parameter (walletId)
	@Test
	void testViewTransactionByType_MissingWalletId() throws Exception {
		mockMvc.perform(get("/transaction/api/v1/view-transactions-by-type").param("type", "SUCCESS")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

	// Invalid enum value for type
	@Test
	void testViewTransactionByType_InvalidType() throws Exception {
		mockMvc.perform(get("/transaction/api/v1/view-transactions-by-type").param("walletId", "wallet123")
				.param("type", "INVALID_TYPE").contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest());
	}

    @Test
    public void givenGetAllTransactionsForWalletIdThenShouldReturnListOfAllTransactionsForWalletId() throws Exception {
transactionList.add(senderTransaction);

        // Mock the service
        when(transactionService.getAllTransactions("wallet123")).thenReturn(transactionList);
transactionList.add(txn1);
        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/transaction/api/v1/all-transactions")
                        .param("walletId", "wallet123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].transactionId").value(3))
                .andExpect(jsonPath("$[1].transactionId").value(1));

        verify(transactionService, times(1)).getAllTransactions("wallet123");
    }

    @Test
    void testGetAllTransactions_WalletNotFound() throws Exception {
        String walletId = "invalidWallet";

        when(transactionService.getAllTransactions(walletId))
                .thenThrow(new WalletNotFoundException("Wallet not found with ID: " + walletId));

        mockMvc.perform(get("/transaction/api/v1/all-transactions")
                        .param("walletId", walletId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Wallet not found with ID: " + walletId));

        verify(transactionService, times(1)).getAllTransactions(walletId);
    }

    @Test
    void testGetAllTransactions_NoTransactionsFound() throws Exception {
        String walletId = "wallet123";

        when(transactionService.getAllTransactions(walletId)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/transaction/api/v1/all-transactions")
                        .param("walletId", walletId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No transactions found for walletId: " + walletId));

        verify(transactionService, times(1)).getAllTransactions(walletId);
    }

}