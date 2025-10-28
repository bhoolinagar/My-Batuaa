package com.batuaa.transactionservice.service;

import com.batuaa.transactionservice.dto.TransactionDateRangeDto;
import com.batuaa.transactionservice.dto.TransactionRemarkDto;
import com.batuaa.transactionservice.dto.TransactionTypeDto;
import com.batuaa.transactionservice.dto.TransferDto;
import com.batuaa.transactionservice.exception.*;
import com.batuaa.transactionservice.model.*;
import com.batuaa.transactionservice.repository.BuyerRepository;
import com.batuaa.transactionservice.repository.TransactionRepository;
import com.batuaa.transactionservice.repository.WalletRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Slf4j
public class TransactionServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private BuyerRepository buyerRepository;
    @Mock
    private TransactionLoggingService transactionLoggingService;


    @InjectMocks
    private TranscationSerivceImp transactionService;

    private TransactionDateRangeDto dateRangeDto;
    private TransactionTypeDto transactionTypeDto;
    private Wallet testWallet;
    private Transaction tx1;
    private Transaction tx2;
    private List<Transaction> transactions;
    private Transaction tx3;
    private Buyer buyer;
    private List<Transaction> transactionslist;

    private Wallet walletFrom;
    private Wallet walletTo;

    private TransferDto transferDto;
    private Buyer buyerFrom;
    private Buyer buyerTo;

    private Wallet walletFrom_s;
    private Buyer fromBuyer_s;
    private Wallet walletTo_s;
    private Transaction transaction;
    private List<Transaction> transactionList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Wallet setup
        testWallet = new Wallet();
        testWallet.setWalletId("WAL0DC01EF4");
        // Date range DTO
        dateRangeDto = new TransactionDateRangeDto();
        dateRangeDto.setWalletId("WAL0DC01EF4");
        dateRangeDto.setEmailId("bhoolinagar@gmail.com");
        dateRangeDto.setStartDate(LocalDate.of(2025, 10, 1));
        dateRangeDto.setEndDate(LocalDate.of(2025, 10, 10));
        dateRangeDto.setPage(0);

        // Transaction type DTO
        transactionTypeDto = new TransactionTypeDto();
        transactionTypeDto.setWalletId("WAL0DC01EF4");
        transactionTypeDto.setEmailId("bhoolinagar@gmail.com");
        transactionTypeDto.setType(Type.RECEIVED);

        // buyer
        buyerFrom = new Buyer();
        buyerFrom.setEmailId("from@example.com");
        buyerTo = new Buyer();
        buyerTo.setEmailId("to@example.com");

        // TransactionType DTO
        transactionTypeDto = new TransactionTypeDto();
        transactionTypeDto.setWalletId("WAL0DC01EF4");
        transactionTypeDto.setEmailId("bhoolinagar@gmail.com");
        transactionTypeDto.setType(Type.RECEIVED);
        // wallets
        walletFrom = new Wallet();
        walletFrom.setWalletId("WALLET_FROM");
        walletFrom.setBuyer(buyerFrom);
        walletFrom.setBalance(new BigDecimal("1000.00"));

        // Date range DTO
        dateRangeDto = new TransactionDateRangeDto();
        dateRangeDto.setWalletId("WAL0DC01EF4");
        dateRangeDto.setEmailId("bhoolinagar@gmail.com");
        dateRangeDto.setStartDate(LocalDate.of(2025, 10, 1));
        dateRangeDto.setEndDate(LocalDate.of(2025, 10, 10));
        dateRangeDto.setPage(0);
        walletTo = new Wallet();
        walletTo.setWalletId("WALLET_TO");
        walletTo.setBuyer(buyerTo);
        walletTo.setBalance(new BigDecimal("500.00"));

        // Wallets
        Wallet wallet1 = new Wallet("WAL0DC01EF4");
        Wallet wallet2 = new Wallet("WAL2ED73EBA");
        Wallet wallet3 = new Wallet("WAL4C9D9DC6");
        Wallet wallet4 = new Wallet("WAL4DA22CC6");
        // Buyers
        Buyer fromBuyer1 = new Buyer();
        fromBuyer1.setEmailId("bhoolinagar@gmail.com");
        Buyer toBuyer1 = new Buyer();
        toBuyer1.setEmailId("anjali@gmail.com");

        Buyer fromBuyer2 = new Buyer();
        fromBuyer2.setEmailId("bhoolinagar@gmail.com");
        Buyer toBuyer2 = new Buyer();
        toBuyer2.setEmailId("khyati@gmail.com");
        Buyer toBuyer3 = new Buyer();
        toBuyer3.setEmailId("antima@gmail.com");
        // Transactions
        tx1 = new Transaction(
                15,
                wallet1,
                wallet2,
                fromBuyer1,
                toBuyer1,
                new BigDecimal("500.00"),
                LocalDateTime.of(2025, 10, 2, 10, 0),
                null,
                "Received Rs500 from wallet WAL4DA22CC6",
                Type.RECEIVED
        );

        tx2 = new Transaction(
                16,
                wallet1,
                wallet3,
                fromBuyer2,
                toBuyer2,
                new BigDecimal("200.00"),
                LocalDateTime.of(2025, 10, 3, 10, 0),
                null,
                "Received Rs200 from wallet WAL4C9D9DC6",
                Type.RECEIVED
        );
        tx3 = new Transaction(
                18, wallet1, wallet4, fromBuyer1, toBuyer3,
                new BigDecimal("500.00"), LocalDateTime.of(2025, 10, 16, 0, 55),
                null, "Money added to wallet WALF729C2F", Type.WITHDRAWN
        );
        // List of transactions
        transactions = List.of(tx1, tx2);
        log.info("Transaction list size: " + transactions.size());
        // TransferDTO
        transferDto = new TransferDto();
        transferDto.setFromWalletId("WALLET_FROM");
        transferDto.setToWalletId("WALLET_TO");
        transferDto.setAmount(new BigDecimal("200.00"));

// sakshi transaction list

        walletFrom_s = new Wallet();
        walletFrom_s.setWalletId("WALLET001");
        walletFrom_s.setBalance(BigDecimal.valueOf(5000));
        walletFrom_s.setCreatedAt(LocalDateTime.now());

        walletTo_s = new Wallet();
        walletTo_s.setWalletId("WALLET002");
        walletTo_s.setBalance(BigDecimal.valueOf(3000));
        walletTo_s.setCreatedAt(LocalDateTime.now());

        fromBuyer_s = new Buyer();
        fromBuyer_s.setEmailId("sakshi@gmail.com");

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

//antima mock data
        Wallet wallet = new Wallet(); // you can use no-arg constructor
        wallet.setWalletId("wallet1");

        buyer = new Buyer();
        buyer.setEmailId("email@example.com");
        transactionslist = Arrays.asList(
                new Transaction(1, wallet, null, buyer, null, new BigDecimal("200.00"),
                        LocalDateTime.now(), Status.SUCCESS, "OK", Type.RECEIVED),
                new Transaction(2, wallet, null, buyer, null, new BigDecimal("500.00"),
                        LocalDateTime.now(), Status.SUCCESS, "OK", Type.RECEIVED),
                new Transaction(3, wallet, null, buyer, null, new BigDecimal("100.00"),
                        LocalDateTime.now(), Status.SUCCESS, "OK", Type.RECEIVED)
        );


    }

    /* Tests for wallet to wallet transfer:
    1) Successful transaction (checks balances, status, remarks, ID)
    2) Sender/receiver wallet not found
    3) Unexpected DB exception (UnableToAddMoneyException)
     */
    @Test
    void testTransferWalletToWallet_GeneratesTransactionId_And_UpdatesStatus_WithFee() {

        // Arrange
        Buyer buyerFrom = new Buyer();
        buyerFrom.setEmailId("from@gmail.com");
        Buyer buyerTo = new Buyer();
        buyerTo.setEmailId("to@gmail.com");

        Wallet walletFrom = new Wallet();
        walletFrom.setWalletId("WALLET_FROM");
        walletFrom.setBuyer(buyerFrom);
        walletFrom.setBalance(new BigDecimal("1000.00"));

        Wallet walletTo = new Wallet();
        walletTo.setWalletId("WALLET_TO");
        walletTo.setBuyer(buyerTo);
        walletTo.setBalance(new BigDecimal("500.00"));

        TransferDto transferDto = new TransferDto();
        transferDto.setFromWalletId("WALLET_FROM");
        transferDto.setToWalletId("WALLET_TO");
        transferDto.setToBuyerEmailId("to@gmail.com");
        transferDto.setAmount(new BigDecimal("200.00"));
        transferDto.setRemarks("Transfer completed");

        when(walletRepository.findByWalletId("WALLET_FROM")).thenReturn(Optional.of(walletFrom));
        when(walletRepository.findByWalletId("WALLET_TO")).thenReturn(Optional.of(walletTo));

        when(transactionRepository.findByFromWalletAndToWalletAndAmountAndStatus(
                any(), any(), any(), eq(Status.PROCESSING))
        ).thenReturn(Optional.empty());

        doAnswer(invocation -> {
            Transaction tx = invocation.getArgument(0);
            if (tx.getTransactionId() == null) {
                tx.setTransactionId((int) (Math.random() * 1000));
            }
            return tx;
        }).when(transactionRepository).save(any(Transaction.class));

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);

        // Act
        Transaction result = transactionService.transferWalletToWallet(transferDto);

        // Assert
        assertNotNull(result.getTransactionId(), "Transaction ID should be generated");
        assertEquals(Status.SUCCESS, result.getStatus(), "Final status should be SUCCESS");
        assertEquals("Transfer completed", result.getRemarks(), "Remarks should match DTO");

        // Verify all saves (PROCESSING + SUCCESS + RECEIVED)
        verify(transactionRepository, atLeast(3)).save(transactionCaptor.capture());
        List<Transaction> allSaved = transactionCaptor.getAllValues();
        assertTrue(allSaved.stream().anyMatch(t -> t.getStatus() == Status.SUCCESS),
                "There should be a SUCCESS transaction saved");

        // --- Fee Calculation ---
        // For Rs.200 → 2% fee = Rs.4.00
        BigDecimal expectedFee = new BigDecimal("4.00");
        BigDecimal expectedSenderFinalBalance = new BigDecimal("1000.00").subtract(new BigDecimal("200.00")).subtract(expectedFee);
        BigDecimal expectedReceiverFinalBalance = new BigDecimal("500.00").add(new BigDecimal("200.00"));

        // Verify balances after fee deduction
        assertEquals(expectedSenderFinalBalance, walletFrom.getBalance(),
                "Sender balance should decrease by amount + fee");
        assertEquals(expectedReceiverFinalBalance, walletTo.getBalance(),
                "Receiver balance should increase by transfer amount");
    }


    @Test
    void testTransferWalletToWallet_SenderWalletNotFound() {

        when(walletRepository.findByWalletId("WALLET_FROM")).thenReturn(Optional.empty());

        WalletNotFoundException exception = assertThrows(
                WalletNotFoundException.class,
                () -> transactionService.transferWalletToWallet(transferDto)
        );

        assertEquals("Sender wallet not found: WALLET_FROM", exception.getMessage());
    }

    @Test
    void testTransferWalletToWallet_ReceiverWalletNotFound() {

        when(walletRepository.findByWalletId("WALLET_FROM")).thenReturn(Optional.of(walletFrom));
        when(walletRepository.findByWalletId("WALLET_TO")).thenReturn(Optional.empty());

        WalletNotFoundException exception = assertThrows(
                WalletNotFoundException.class,
                () -> transactionService.transferWalletToWallet(transferDto)
        );

        assertEquals("Receiver wallet not found: WALLET_TO", exception.getMessage());
    }


    // Negative test for unexpected exception


    /*
    1) Successful fetch with results
    2) No transactions found : TransactionNotFoundException
    3) Unexpected error :  UnableToFilterByRemarkException
     */
    @Test
    void testFilterTransactionsByRemark_success() {

        TransactionRemarkDto dto = new TransactionRemarkDto();
        dto.setWalletId("WALLET_FROM");
        dto.setEmailId("from@example.com");
        dto.setRemark("test");

        Transaction tx = new Transaction();
        tx.setTransactionId(1);
        tx.setRemarks("test remark");

        when(transactionRepository.findByWalletAndEmailAndRemarkNative(
                "WALLET_FROM", "from@example.com", "test")).thenReturn(List.of(tx));

        List<Transaction> result = transactionService.filterTransactionsByRemark(dto);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test remark", result.get(0).getRemarks());
    }
    @Test
    void testFilterTransactionsByRemark_noTransactionsFound() {

        TransactionRemarkDto dto = new TransactionRemarkDto();
        dto.setWalletId("WALLET_FROM");
        dto.setEmailId("from@example.com");
        dto.setRemark("unknown");

        when(transactionRepository.findByWalletAndEmailAndRemarkNative(
                "WALLET_FROM", "from@example.com", "unknown")).thenReturn(List.of());

        TransactionNotFoundException ex = assertThrows(TransactionNotFoundException.class,
                () -> transactionService.filterTransactionsByRemark(dto));
        assertEquals("No transaction found for wallet WALLET_FROM with remark: unknown", ex.getMessage());
    }

    @Test
    void testFilterTransactionsByRemark_ThrowsUnableToFilterByRemarkException() {

        TransactionRemarkDto dto = new TransactionRemarkDto();
        dto.setWalletId("wallet123");
        dto.setRemark("testRemark");
        dto.setEmailId("test@example.com");

        when(transactionRepository.findByWalletAndEmailAndRemarkNative(
                dto.getWalletId(),
                dto.getEmailId(),
                dto.getRemark()))
                .thenThrow(new UnableToFilterByRemarkException("DB error"));

        assertThrows(UnableToFilterByRemarkException.class, () ->
                transactionService.filterTransactionsByRemark(dto)
        );
    }

    // ===================== Filter by Type Tests =====================

    @Test
    void testViewTransactionsByType_success() {
        // Wallet exists
        when(walletRepository.existsByIdAndEmailId(transactionTypeDto.getWalletId(), transactionTypeDto.getEmailId()))
                .thenReturn(true);

        // Mock repository call
        when(transactionRepository.findByToWalletIdAndType(
                transactionTypeDto.getWalletId(),

                transactionTypeDto.getType()))
                .thenReturn(List.of(tx1));

        // When
        List<Transaction> result = transactionService.viewTransactionsByType(transactionTypeDto);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Type.RECEIVED, result.get(0).getType());
        assertEquals("WAL0DC01EF4", result.get(0).getFromWallet().getWalletId());
        assertEquals("Received Rs500 from wallet WAL4DA22CC6", result.get(0).getRemarks());

        // Verify repository call
        verify(transactionRepository, times(1))
                .findByToWalletIdAndType(
                        transactionTypeDto.getWalletId(),

                        transactionTypeDto.getType());
    }

    //for check filter by withrowa

    @Test
    void testViewTransactionsByType_emptyList() {
        when(walletRepository.existsByIdAndEmailId(transactionTypeDto.getWalletId(), transactionTypeDto.getEmailId()))
                .thenReturn(true);
        when(transactionRepository.findByToWalletIdAndType(
                transactionTypeDto.getWalletId(),
                transactionTypeDto.getType()))
                .thenReturn(Collections.emptyList());

        EmptyTransactionListException exception = assertThrows(
                EmptyTransactionListException.class,
                () -> transactionService.viewTransactionsByType(transactionTypeDto)
        );
        assertEquals("No transactions found for wallet: WAL0DC01EF4 and type: RECEIVED", exception.getMessage());
        verify(transactionRepository, times(1))
                .findByToWalletIdAndType(
                        transactionTypeDto.getWalletId(),
                        transactionTypeDto.getType());
    }

    @Test
    void testViewTransactionsByType_withdrawal_success() {
        // Set up TransactionTypeDto for WITHDRAWN
        TransactionTypeDto withdrawalDto = new TransactionTypeDto();
        withdrawalDto.setWalletId("WAL0DC01EF4");
        withdrawalDto.setEmailId("bhoolinagar@gmail.com");
        withdrawalDto.setType(Type.WITHDRAWN);

        // Mock wallet existence
        when(walletRepository.existsByIdAndEmailId(withdrawalDto.getWalletId(), withdrawalDto.getEmailId()))
                .thenReturn(true);

        // Mock transactionRepository to return only WITHDRAWN transaction
        when(transactionRepository.findByFromWalletIdAndType(withdrawalDto.getWalletId(), Type.WITHDRAWN))
                .thenReturn(List.of(tx3)); // WITHDRAWN transaction

        // Call service method
        List<Transaction> result = transactionService.viewTransactionsByType(withdrawalDto);

        // Assertions
        assertEquals(1, result.size());
        assertEquals(Type.WITHDRAWN, result.get(0).getType());
        assertEquals(18, result.get(0).getTransactionId());
        assertEquals(new BigDecimal("500.00"), result.get(0).getAmount());
        assertEquals("Money added to wallet WALF729C2F", result.get(0).getRemarks());

        // Verify repository call
        verify(transactionRepository, times(1))
                .findByFromWalletIdAndType(withdrawalDto.getWalletId(), Type.WITHDRAWN);
    }
    // ===================== Date Range Tests =====================

    @Test
    void testWalletNotFound() {
        when(walletRepository.existsByIdAndEmailId(anyString(), anyString())).thenReturn(false);

        WalletNotFoundException exception = assertThrows(WalletNotFoundException.class,
                () -> transactionService.findByWalletIdAndDateBetween(dateRangeDto));

        assertEquals("Wallet not found: WAL0DC01EF4", exception.getMessage());
    }

    @Test
    void testStartDateAfterEndDate() {
        dateRangeDto.setStartDate(LocalDate.of(2025, 10, 11));
        dateRangeDto.setEndDate(LocalDate.of(2025, 10, 10));

        when(walletRepository.existsByIdAndEmailId(anyString(), anyString())).thenReturn(true);

        DateTimeException exception = assertThrows(DateTimeException.class,
                () -> transactionService.findByWalletIdAndDateBetween(dateRangeDto));

        assertEquals("Start date cannot be after end date.", exception.getMessage());
    }

    @Test
    void testFutureDate() {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        dateRangeDto.setStartDate(futureDate);
        dateRangeDto.setEndDate(futureDate);

        when(walletRepository.existsByIdAndEmailId(anyString(), anyString())).thenReturn(true);

        DateTimeException exception = assertThrows(DateTimeException.class,
                () -> transactionService.findByWalletIdAndDateBetween(dateRangeDto));

        assertEquals("Start and end dates must not be in the future.", exception.getMessage());
    }

    @Test
    void testEmptyTransactionList() {
        when(walletRepository.existsByIdAndEmailId(anyString(), anyString())).thenReturn(true);

        Page<Transaction> emptyPage = new PageImpl<>(Collections.emptyList());
        when(transactionRepository.findByWalletAndTimestampRange(anyString(), any(), any(), any())).thenReturn(emptyPage);

        EmptyTransactionListException exception = assertThrows(EmptyTransactionListException.class,
                () -> transactionService.findByWalletIdAndDateBetween(dateRangeDto));

        assertEquals("No transactions found for the given date range.", exception.getMessage());
    }

    @Test
    void testValidTransactionList() {
        when(walletRepository.existsByIdAndEmailId(anyString(), anyString())).thenReturn(true);

        Page<Transaction> page = new PageImpl<>(transactions);

        when(transactionRepository.findByWalletAndTimestampRange(anyString(), any(), any(), any())).thenReturn(page);

        List<Transaction> result = transactionService.findByWalletIdAndDateBetween(dateRangeDto);
        log.info("Result list size: " + result.size());
        assertEquals(2, result.size());
        // Ensure descending order by timestamp
        assertTrue(result.get(0).getTimestamp().isAfter(result.get(1).getTimestamp()));
    }

    @Test
    public void givenGetAllTransactionsForWalletIdThenShouldReturnListOfAllTransactionsForWalletId()
            throws EmptyTransactionListException {

        transactionRepository.save(transaction);
        when(transactionRepository.findByEmailAndWallet("sakshi@gmail.com", "sakshi@gmail.com", "WALLET001", "WALLET001")).thenReturn(transactionList);
        List<Transaction> transactionList1 = transactionService.getAllTransactions("sakshi@gmail.com", "WALLET001");
        assertEquals(transactionList, transactionList1);
        verify(transactionRepository, times(1)).save(transaction);
        verify(transactionRepository, times(1)).findByEmailAndWallet("sakshi@gmail.com", "sakshi@gmail.com", "WALLET001", "WALLET001");

    }

    // test cases for admin
    @Test
    void testFindByAdminEmailAndRole_AdminExists_ReturnsTransactions() {
        String email = "admin@example.com";
        Role role = Role.ADMIN;

        when(buyerRepository.existsByEmailIdAndRole(email, role)).thenReturn(true);
        when(transactionRepository.findAll()).thenReturn(transactions);

        List<Transaction> result = transactionService.findByAdminEmailAndRole(email, role);

        assertEquals(transactions.size(), result.size());
        verify(buyerRepository, times(1)).existsByEmailIdAndRole(email, role);
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    void testFindByAdminEmailAndRole_AdminNotFound_ThrowsException() {
        String email = "unknown@example.com";
        Role role = Role.ADMIN;

        when(buyerRepository.existsByEmailIdAndRole(email, role)).thenReturn(false);

        AdminNotFoundException ex = assertThrows(AdminNotFoundException.class,
                () -> transactionService.findByAdminEmailAndRole(email, role));
        assertEquals("Admin not found with email: " + email, ex.getMessage());

        verify(transactionRepository, never()).findAll();
    }


    // Transaction amount

    @Test
    void testSortTransactionsByAmountAscending() {
        when(transactionRepository.findTransactionAmountByWalletAndEmail("wallet1", "email@example.com"))
                .thenReturn(transactionslist);

        List<Transaction> sorted = transactionService.sortTransactionsByAmount("wallet1", "email@example.com", "ASC");

        assertEquals(3, sorted.size());
        assertEquals(new BigDecimal("100.00"), sorted.get(0).getAmount());
        assertEquals(new BigDecimal("200.00"), sorted.get(1).getAmount());
        assertEquals(new BigDecimal("500.00"), sorted.get(2).getAmount());
    }

    @Test
    void testSortTransactionsByAmountDescending() {
        when(transactionRepository.findTransactionAmountByWalletAndEmail("wallet1", "email@example.com"))
                .thenReturn(transactionslist);

        List<Transaction> sorted = transactionService.sortTransactionsByAmount("wallet1", "email@example.com", "DESC");

        assertEquals(3, sorted.size());
        assertEquals(new BigDecimal("500.00"), sorted.get(0).getAmount());
        assertEquals(new BigDecimal("200.00"), sorted.get(1).getAmount());
        assertEquals(new BigDecimal("100.00"), sorted.get(2).getAmount());
    }

    @Test
    void testSortTransactionsByAmountInvalidSortOrderDefaultsToAscending() {
        when(transactionRepository.findTransactionAmountByWalletAndEmail("wallet1", "email@example.com"))
                .thenReturn(transactionslist);

        List<Transaction> sorted = transactionService.sortTransactionsByAmount("wallet1", "email@example.com", "INVALID");

        // Should behave as ascending
        assertEquals(new BigDecimal("100.00"), sorted.get(0).getAmount());
    }

}


