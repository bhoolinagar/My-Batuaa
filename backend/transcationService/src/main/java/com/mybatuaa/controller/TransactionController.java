package com.mybatuaa.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.mybatuaa.exception.WalletNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mybatuaa.exception.EmptyTransactionListException;
import com.mybatuaa.model.Status;
import com.mybatuaa.model.Transaction;
import com.mybatuaa.service.TransactionService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("transaction/api/v1")
public class TransactionController {

    @Autowired
	private TransactionService transactionService;

	@Autowired
	public TransactionController(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	// getAllTransactions(String walletId)
	@GetMapping("/all-transactions")
	public ResponseEntity<?> getAllTransaction(@RequestParam String walletId) {
        try {
            log.info("Request received to fetch all transactions for walletId={}", walletId);
            List<Transaction> transactions = transactionService.getAllTransactions(walletId);

            if (transactions.isEmpty()) {
                log.warn("No transactions found for walletId={}", walletId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No transactions found for walletId: " + walletId);
            }

            return ResponseEntity.ok(transactions);

        } catch (WalletNotFoundException e) {
            log.error("Wallet not found: {}", walletId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (RuntimeException e) {
            log.error("Failed to fetch transactions for walletId={}", walletId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching transactions. Please try again later.");
        }

    }


	/*
	 * addMoneyFromBank( String walletIdto, accountNumber, BigDecimal Amount)
	 *
	 */

	@PostMapping("/add-money-from-bank")
	public ResponseEntity<?> addMoneyFromBank(@RequestParam String walletIdto, @RequestParam String accountNumber,
			@RequestParam BigDecimal amount) {
        //No need to add walletId inside add Money//
		Transaction transaction = transactionService.addMoney(walletIdto, accountNumber, amount);
		return new ResponseEntity<>(transaction, HttpStatus.CREATED);
	}

	/*
	 * TransferWalletToWallet(walletIdFrom, walletIdTo, BigDecimal Amount)
	 */
	@PostMapping("/transfer-wallet-to-wallet")
	public ResponseEntity<?> transferMoneywalletToWallet(@RequestParam String walletIdFrom,
			@RequestParam String walletIdTo, @RequestParam BigDecimal amount) {
		Transaction transaction = transactionService.transferWalletToWallet(walletIdFrom, walletIdTo, amount);
		return new ResponseEntity<>(transaction, HttpStatus.CREATED);
	}

	/*
	 * viewTransactionByRemark (based on walletId, remark)
	 */
	@GetMapping("/view-transactions-by-remark")
	public ResponseEntity<?> viewTransactionByRemark(@RequestParam String walletId, @RequestParam String remark) {
		List<Transaction> transactionList = transactionService.filterTransactionsByRemark(walletId, remark);
		return new ResponseEntity<>(transactionList, HttpStatus.OK);
	}

	// View transaction by Type (based on type)
	@GetMapping("/view-transactions-by-type")
	public ResponseEntity<?> viewTransactionByType(@RequestParam String walletId, @RequestParam Status type) {
		try {
			log.info("Fetching transactions for wallet {} and type {}", walletId, type);
			List<Transaction> transactions = transactionService.filterTransactionByType(walletId, type);
			return new ResponseEntity<>(transactions, HttpStatus.OK);
		} catch (EmptyTransactionListException e) {
			log.error("No transactions found for wallet {} and type {}", walletId, type, e);
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("Internal error while fetching transactions for wallet {} and type {}", walletId, type, e);
			return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// View transaction history between startDate and endDate for a wallet
	@GetMapping("/view-transactions-by-date")
	public ResponseEntity<?> viewTransactionByDateRange(@RequestParam String walletId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

		try {
			List<Transaction> transactions = transactionService.filterTransactionsByDateRange(walletId, startDate,
					endDate);
			return ResponseEntity.ok(transactions);
		} catch (RuntimeException e) {
			log.error("Error retrieving transactions", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}
	// sortingByAmount(based on amount)
	@GetMapping("/view-transactions-by-amount/{amount}")
	public ResponseEntity<?> viewTransactionByAmount(BigDecimal amount) {
		return null;
	}
	// Free bracket calculator-chargeable(amount)

}
