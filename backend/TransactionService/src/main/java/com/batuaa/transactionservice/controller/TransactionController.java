package com.batuaa.transactionservice.controller;

import com.batuaa.transactionservice.dto.*;
import com.batuaa.transactionservice.exception.EmptyTransactionListException;
import com.batuaa.transactionservice.model.Transaction;
import com.batuaa.transactionservice.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController

@RequestMapping("/transaction/api/v2")
public class TransactionController {

    @Autowired
    private final TransactionService transactionService;
@Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Get all transactions for a specific wallet (sorted by most recent)
     */
    @GetMapping("/all-transactions")
    public ResponseEntity<?> getAllTransactions(@RequestParam String emailId,@RequestParam String walletId) {
        log.info("Fetching all transactions for walletId: {}", walletId);
        try {
            return ResponseEntity.ok(transactionService.getAllTransactions(emailId, walletId));
        }
        catch(EmptyTransactionListException e){
            return  new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Transfer money from one wallet to another
     */
    @PostMapping("/transfer-wallet-to-wallet")
    public ResponseEntity<?> transferWalletToWallet(@Valid @RequestBody TransferDto transferDto) {
        log.info("Initiating wallet-to-wallet transfer: {}", transferDto);
        return ResponseEntity.ok(transactionService.transferWalletToWallet(transferDto));
    }

    /**
     * View transaction by remark and transactionId
     */
    @GetMapping("/view-transactions-by-remark")
    public ResponseEntity<?> viewTransactionByRemark(@RequestBody TransactionRemarkDto transactionRemarkDto) {
        log.info("Fetching transaction by remark: {}", transactionRemarkDto.getRemark());
        return ResponseEntity.ok(transactionService.filterTransactionsByRemark(transactionRemarkDto ));
    }

    /**
     * View transactions by type (DEBIT or CREDIT)
     */
    @PostMapping("/filter-by-type")
    public ResponseEntity<ApiResponse> filterByType(
            @Valid @RequestBody TransactionTypeDto transactionTypeDto) {

        log.info("Filtering transactions for wallet {} by type {}",
                transactionTypeDto.getWalletId(), transactionTypeDto.getType());

        List<Transaction> transactions = transactionService.viewTransactionsByType(transactionTypeDto);

        ApiResponse response = new ApiResponse(
                200,
                transactions.isEmpty()
                        ? "No transactions found for the selected type"
                        : "Transactions fetched successfully",
                transactions
        );

        return ResponseEntity.ok(response);
    }

    /**
     * View transaction history between startDate and endDate for a wallet
     */

    @PostMapping("/transactions/filter-by-date")
    public ResponseEntity<ApiResponse> filterTransactionsByDate(
            @Valid @RequestBody TransactionDateRangeDto dto) {
        List<Transaction> transactions = transactionService.findByWalletIdAndDateBetween(dto);
        ApiResponse response = new ApiResponse(200, "Transactions fetched successfully", transactions);
        return ResponseEntity.ok(response);
    }

    /**
     * View transactions sorted by amount (ascending or descending)
     */
    @GetMapping("/view-transactions-by-amount")
    public ResponseEntity<?> viewTransactionsByAmount(@RequestParam String walletId,
                                                      @RequestParam(defaultValue = "DESC") String sortOrder) {
        log.info("Fetching transactions by amount for walletId: {} sorted by: {}", walletId, sortOrder);
        return ResponseEntity.ok(transactionService.sortTransactionsByAmount(walletId, sortOrder));
    }
}
