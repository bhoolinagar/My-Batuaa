package com.batuaa.transactionservice.controller;

import com.batuaa.transactionservice.dto.*;
import com.batuaa.transactionservice.exception.EmptyTransactionListException;
import com.batuaa.transactionservice.model.Role;
import com.batuaa.transactionservice.model.Transaction;
import com.batuaa.transactionservice.service.TransactionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/transaction/api/v2")
public class TransactionController {

    //   @Autowired
    private TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

	/**
	 * Get all transactions for a specific wallet (sorted by most recent)
	 */
	@GetMapping("/all-transactions")
	public ResponseEntity<?> getAllTransactions(@RequestParam String emailId, @RequestParam String walletId) {
        log.info("Fetching all transactions for walletId: {}", walletId);
        try {
            return ResponseEntity.ok(transactionService.getAllTransactions(emailId, walletId));
        } catch (EmptyTransactionListException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Transfer money from one wallet to another
     */
    @PostMapping("/transfer-wallet")
    public ResponseEntity<ApiResponse> transferWalletToWallet(@Valid @RequestBody TransferDto transferDto) {
        log.info("Initiating wallet-to-wallet transfer: {}", transferDto);
        Transaction transaction = transactionService.transferWalletToWallet(transferDto);
     /*   return ResponseEntity.ok(ApiResponse.builder()
                .statusCode(200)
                .message("Transaction completed successfully")
                .data(transaction).build());

      */

        ApiResponse response = new ApiResponse("success", "Transaction completed successfully", transaction);
        return ResponseEntity.ok(response);
    }

    /**
     * View transaction by remark and transactionId
     */
//    HTTP GET doesn’t support a body, so @RequestBody TransactionRemarkDto won’t work
//     solution figured was to use @ModelAttribute, which binds query parameters to a DTO automatically.
    @PostMapping("/view-transactions-by-remark")
    public ResponseEntity<ApiResponse> viewTransactionByRemark(
            @Valid @RequestBody TransactionRemarkDto transactionRemarkDto) {

        log.info("Filtering transactions by remark: {}"
                , transactionRemarkDto.getRemark());
        List<Transaction> transactions = transactionService.filterTransactionsByRemark(transactionRemarkDto);

        /*return ResponseEntity.ok(ApiResponse.builder()
                ."success"
                .message("Filtered transactions successfully")
                .data(transactions)
                .build());
 */

        ApiResponse response = new ApiResponse(
                "success",
                transactions.isEmpty()
                        ? "No transactions found for the selected remark"
                        : "Filtered transactions successfully",
                transactions
        );
        return ResponseEntity.ok(response);
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
                "success",
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

    @PostMapping("/filter-by-date")
    public ResponseEntity<ApiResponse> filterTransactionsByDate(
            @Valid @RequestBody TransactionDateRangeDto dto) {
        List<Transaction> transactions = transactionService.findByWalletIdAndDateBetween(dto);
        ApiResponse response = new ApiResponse("success", "Transactions fetched successfully", transactions);
        return ResponseEntity.ok(response);
    }

    /**
     * View transactions sorted by amount (ascending or descending)
     */
    @GetMapping("/view-transactions-by-amount")
    public ResponseEntity<?> viewTransactionsByAmount(@RequestParam String walletId, @RequestParam String emailId
            , @RequestParam(defaultValue = "DESC") String sortOrder) {
        log.info("Fetching transactions by amount for walletId: {} sorted by: {}", walletId, emailId);
        return ResponseEntity.ok(transactionService.sortTransactionsByAmount(walletId, emailId, sortOrder));
    }

    /**
     * get transactions list for admin by email, role
     */

    @GetMapping("/admin-transactions")
    public ResponseEntity<ApiResponse> getAllTransactionByAdmin(@RequestParam String email, @RequestParam Role role) {
        List<Transaction> transactions = transactionService.findByAdminEmailAndRole(email, role);
        ApiResponse response = new ApiResponse(
                "success",
                transactions.isEmpty()
                        ? "No transactions found."
                        : "Transactions retrieved successfully",
                transactions
        );

        return ResponseEntity.ok(response);

    }
}
