package com.mybatuaa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.mybatuaa.exception.AmountCanNotBeNullException;
import com.mybatuaa.exception.EmptyTransactionListException;
import com.mybatuaa.exception.ErrorOccurredUnableToFilterByRemarkException;
import com.mybatuaa.exception.InsufficientFundsException;
import com.mybatuaa.exception.WalletNotFoundException;
import com.mybatuaa.model.BankAccount;
import com.mybatuaa.model.Status;
import com.mybatuaa.model.Transaction;
import com.mybatuaa.model.Wallet;
import com.mybatuaa.repository.BankAccountResposity;
import com.mybatuaa.repository.TransactionRepository;
import com.mybatuaa.repository.WalletRepository;

/* @SpringBootTest(classes = TransactionServiceApplicationTests.class) | once integrated will modify as needed commenting because
methods required to be tested using Mocked data only, SpringBootTest injecting actual entities hence commented */
@Slf4j
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

	// for user walletId
	private String walletId;
	// for translations
	private Transaction txn1;
	private Transaction txn2;
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

		// for transactions
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

        transaction = new Transaction();
        transaction.setTransactionId(1);
        transaction.setFromWallet(walletFrom);
        transaction.setToWallet(walletTo);
        transaction.setAmount(new BigDecimal("200"));
        transaction.setStatus(Status.SUCCESS);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setRemarks("Transferred Rs200 to wallet wallet456");
        transactionList= new ArrayList<>();

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

		WalletNotFoundException ex = assertThrows(WalletNotFoundException.class,
				() -> transactionService.addMoney("WALLET999", "12345", BigDecimal.valueOf(1000)));

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

		AmountCanNotBeNullException ex = assertThrows(AmountCanNotBeNullException.class,
				() -> transactionService.transferWalletToWallet("WALLET001", "WALLET002", null));

		assertEquals("Amount Should Not Be Null", ex.getMessage());

		verify(walletRepository, never()).findByWalletId(any());
		verify(walletRepository, never()).save(any());
		verify(transactionRepository, never()).save(any());
	}

	@Test
	void testTransferWalletToWallet_senderNotFound() {

		when(walletRepository.findByWalletId("WALLET999")).thenReturn(Optional.empty());

		WalletNotFoundException exception = assertThrows(WalletNotFoundException.class,
				() -> transactionService.transferWalletToWallet("WALLET999", "WALLET002", BigDecimal.valueOf(1000)));

		assertEquals("Sender wallet not found: WALLET999", exception.getMessage());

		verify(walletRepository).findByWalletId("WALLET999");
		verify(walletRepository, never()).save(any(Wallet.class));
		verify(transactionRepository, never()).save(any(Transaction.class));
	}

	@Test
	void testTransferWalletToWallet_receiverNotFound() {

		when(walletRepository.findByWalletId("WALLET001")).thenReturn(Optional.of(walletFrom));
		when(walletRepository.findByWalletId("WALLET999")).thenReturn(Optional.empty());

		WalletNotFoundException exception = assertThrows(WalletNotFoundException.class,
				() -> transactionService.transferWalletToWallet("WALLET001", "WALLET999", BigDecimal.valueOf(1000)));

		assertEquals("Receiver wallet not foundWALLET999", exception.getMessage());

		verify(walletRepository).findByWalletId("WALLET001");
		verify(walletRepository).findByWalletId("WALLET999");
		verify(walletRepository, never()).save(any(Wallet.class));
		verify(transactionRepository, never()).save(any(Transaction.class));
	}

	@Test
	void testTransferWalletToWallet_insufficientFunds() {
		BigDecimal amount = BigDecimal.valueOf(6000);
                                           //WALLET001
		when(walletRepository.findByWalletId("WALLET001")).thenReturn(Optional.of(walletFrom));
		when(walletRepository.findByWalletId("WALLET002")).thenReturn(Optional.of(walletTo));

		InsufficientFundsException exception = assertThrows(InsufficientFundsException.class,
				() -> transactionService.transferWalletToWallet("WALLET001", "WALLET002", amount));

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
	void testFilterTransactionsByRemark_success() {
		 walletId = "WALLET001";
		String remark = "transferred";
		Transaction testTransaction = new Transaction();
		testTransaction.setRemarks("Transferred");
		testTransaction.setFromWallet(walletFrom);
		testTransaction.setToWallet(walletTo);
		when(walletRepository.findByWalletId(walletId)).thenReturn(Optional.of(walletFrom));
		when(transactionRepository.findByWalletAndRemarkContainingIgnoreCase(walletId, remark))
		.thenReturn(List.of(testTransaction));
		List<Transaction> result = transactionService.filterTransactionsByRemark(walletId, remark);
		assertNotNull(result);
		assertEquals(1, result.size());
		assertTrue(result.get(0).getRemarks().toLowerCase().contains(remark.toLowerCase()));

		verify(walletRepository).findByWalletId(walletId);
		verify(transactionRepository).findByWalletAndRemarkContainingIgnoreCase(walletId, remark);
	}

	@Test
	void testFilterTransactionsByRemark_noTransactionsFound() {

		String walletId = "WALLET001";
		String remark = "nonexistent";

		when(walletRepository.findByWalletId(walletId)).thenReturn(Optional.of(walletFrom));
		when(transactionRepository.findByWalletAndRemarkContainingIgnoreCase(walletId, remark)).thenReturn(List.of());

		ErrorOccurredUnableToFilterByRemarkException ex = assertThrows(
				ErrorOccurredUnableToFilterByRemarkException.class,
				() -> transactionService.filterTransactionsByRemark(walletId, remark));
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

		WalletNotFoundException ex = assertThrows(WalletNotFoundException.class,
				() -> transactionService.filterTransactionsByRemark(walletId, remark));

		assertEquals("Wallet Not Found or is invalid", ex.getMessage());

		verify(walletRepository, never()).findByWalletId(any());
		verify(transactionRepository, never()).findByWalletAndRemarkContainingIgnoreCase(any(), any());
	}

	// Test successful fetch start and end date
	@Test
	void testFindByWalletIdAndDateBetween_success() {
		LocalDate start = LocalDate.now().minusDays(5);
		LocalDate end = LocalDate.now().minusDays(1);

		LocalDateTime startOfDay = start.atStartOfDay();
		LocalDateTime endOfDay = end.atTime(LocalTime.MAX);
		Transaction tx = new Transaction();
		tx.setAmount(BigDecimal.valueOf(100));
		when(walletRepository.existsById(walletId)).thenReturn(true);
		when(transactionRepository.findByFromWallet_WalletIdOrToWallet_WalletIdAndTimestampBetween(walletId, walletId,
				startOfDay, endOfDay)).thenReturn(List.of(tx));
		List<Transaction> result = transactionService.filterTransactionsByDateRange(walletId, start, end);

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(BigDecimal.valueOf(100), result.get(0).getAmount());
		verify(walletRepository).existsById(walletId);
		verify(transactionRepository).findByFromWallet_WalletIdOrToWallet_WalletIdAndTimestampBetween(walletId,
				walletId, startOfDay, endOfDay);

	}

	// Test wallet not found
	@Test
	void testFindByWalletIdAndDateBetween_WalletNotFound() {
		when(walletRepository.existsById(walletId)).thenReturn(false);

		LocalDate start = LocalDate.now().minusDays(5);
		LocalDate end = LocalDate.now().minusDays(1);

		assertThrows(WalletNotFoundException.class,
				() -> transactionService.filterTransactionsByDateRange(walletId, start, end));
	}

	// Test invalid date range (start after end)
	@Test
	void testFindByWalletIdAndDateBetween_StartAfterEnd() {
		when(walletRepository.existsById(walletId)).thenReturn(true);

		LocalDate start = LocalDate.now().minusDays(1);
		LocalDate end = LocalDate.now().minusDays(5);

		assertThrows(DateTimeException.class,
				() -> transactionService.filterTransactionsByDateRange(walletId, start, end));
	}

	// Test future dates (start or end after current date)
	@Test
	void testFindByWalletIdAndDateBetween_FutureDates() {
		when(walletRepository.existsById(walletId)).thenReturn(true);

		LocalDate start = LocalDate.now().plusDays(1);
		LocalDate end = LocalDate.now().plusDays(3);

		assertThrows(DateTimeException.class,
				() -> transactionService.filterTransactionsByDateRange(walletId, start, end));
	}

	// Test unexpected exception (should return empty list)
	@Test
	void testFindByWalletIdAndDateBetween_UnexpectedException() {
		LocalDate start = LocalDate.now().minusDays(5);
		LocalDate end = LocalDate.now().minusDays(1);

		when(walletRepository.existsById(walletId)).thenReturn(true);
		when(transactionRepository.findByFromWallet_WalletIdOrToWallet_WalletIdAndTimestampBetween(anyString(),
				anyString(), any(), any())).thenThrow(new RuntimeException("DB error"));

		List<Transaction> result = transactionService.filterTransactionsByDateRange(walletId, start, end);

		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	// ----Transaction by Type- success, failed, processing
	// Test successful retrieval of transactions
	@Test
	void testViewTransactionByType_Success() {
		when(transactionRepository.findByFromWallet_WalletIdAndStatus("WALLET001", Status.SUCCESS))
		.thenReturn(Arrays.asList(txn1, txn2));

		List<Transaction> result = transactionService.filterTransactionByType("WALLET001", Status.SUCCESS);

		assertNotNull(result);
		assertEquals(2, result.size());
		verify(transactionRepository, times(1)).findByFromWallet_WalletIdAndStatus("WALLET001", Status.SUCCESS);
	}

	// Test empty transaction list throws exception
	@Test
	void testViewTransactionByType_EmptyList_ThrowsException() {
		when(transactionRepository.findByFromWallet_WalletIdAndStatus("WALLET001", Status.SUCCESS))
		.thenReturn(Collections.emptyList());

		assertThrows(EmptyTransactionListException.class,
				() -> transactionService.filterTransactionByType("WALLET001", Status.SUCCESS));
	}

	// Test null transaction list throws exception
	@Test
	void testViewTransactionByType_NullList_ThrowsException() {
		when(transactionRepository.findByFromWallet_WalletIdAndStatus("WALLET003", Status.SUCCESS)).thenReturn(null);

		assertThrows(EmptyTransactionListException.class,
				() -> transactionService.filterTransactionByType("WALLET003", Status.SUCCESS));
	}

	// Test sorting order by timestamp
	@Test
	void testViewTransactionByType_SortedByTimestamp() {
		when(transactionRepository.findByFromWallet_WalletIdAndStatus("WALLET001", Status.SUCCESS))
		.thenReturn(Arrays.asList(txn1, txn2));

		List<Transaction> result = transactionService.filterTransactionByType("WALLET001", Status.SUCCESS);

		// txn2 (older date) should come before txn1
		assertEquals(2, result.get(0).getTransactionId());
		assertEquals(1, result.get(1).getTransactionId());
	}

	// Test repository is called with correct parameters
	@Test
	void testViewTransactionByType_RepositoryCallVerification() {
		when(transactionRepository.findByFromWallet_WalletIdAndStatus(anyString(), any(Status.class)))
		.thenReturn(Collections.singletonList(txn1));

		transactionService.filterTransactionByType("WALLET001", Status.FAILED);

		verify(transactionRepository, times(1)).findByFromWallet_WalletIdAndStatus("WALLET001", Status.FAILED);
	}

/*
    @Test
    void givenGetAllTransactionsForWalletIdThenShouldReturnListOfAllTransactionsForWalletId() {
transactionList.add(transaction);
walletId="WALLET001";
log.info("Error messafe:  ------: "+ transactionList.get(0).getFromWallet().getWalletId());
        //transactionRepository.save(transaction);
        when(transactionRepository.findByFromWallet_WalletIdOrToWallet_WalletId("WALLET001","WALLET001")).thenReturn(transactionList);
        List<Transaction> transactionList1 = transactionService.getAllTransactions(walletId);
        assertEquals(transactionList,transactionList1);
        verify(transactionRepository,times(1)).save(transaction);
        verify(transactionRepository, times(1)).findByFromWallet_WalletIdOrToWallet_WalletId("WALLET001","WALLET001");

    }

    @Test
    void testGetAllTransactions_WalletNotFound() {
        String walletId = "invalidWallet";

        when(walletRepository.existsById(walletId)).thenReturn(false);

        WalletNotFoundException exception = assertThrows(WalletNotFoundException.class, () ->
                transactionService.getAllTransactions(walletId));

        assertEquals("Wallet not found with ID: " + walletId, exception.getMessage());
        verify(walletRepository, times(1)).existsById(walletId);
        verify(transactionRepository, never()).findByFromWallet_WalletIdOrToWallet_WalletId(any(),any());
    }

 */
}
