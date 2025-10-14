package com.mybatuaa.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mybatuaa.exception.BankAccountAlreadyLinkedException;
import com.mybatuaa.exception.WalletNotFoundException;
import com.mybatuaa.model.BankAccount;
import com.mybatuaa.service.BankService;
import com.mybatuaa.service.WalletService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("wallet/api/v1")
public class WalletController {

	 @Autowired
	private WalletService walletService;
	 @Autowired
	private BankService bankService;

	// BigDecimal getBalance( String walletId)
	@GetMapping("/get-wallet-balance/{walletId}")
	public ResponseEntity<?> getWalletBalance(@PathVariable String walletId) {
		try {
			log.info("Received request to fetch balance for walletId: {}", walletId);
			BigDecimal balance = walletService.getWalletBalance(walletId);
			return ResponseEntity.ok(balance);
		} catch (WalletNotFoundException e) {
			log.error("Wallet not found for walletId {} : {}", e, walletId);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (Exception e) {
			log.error("Error fetching wallet balance for walletID: {}", walletId, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("failed to fetch wallet balance,Please try again later");
		}

	}

	// to link new bank account
	@PostMapping("/link-bank-account")
	public ResponseEntity<?> linkBankAccountToWallet(@RequestParam String walletId,
			@RequestBody BankAccount bankAccount) {
		try {
			log.info("Linking bank account {} to wallet {}", bankAccount.getAccountNumber(), walletId);
			BankAccount linkedAccount = bankService.linkBankAccountToWallet(walletId, bankAccount);
			return new ResponseEntity<>(linkedAccount, HttpStatus.CREATED);
		} catch (WalletNotFoundException e) {
			log.error("Wallet not found: {}", walletId, e);
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (BankAccountAlreadyLinkedException e) {
			log.error("Bank account already linked: {}", bankAccount.getAccountNumber(), e);
			return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
		} catch (Exception e) {
			log.error("Unexpected error while linking bank account {} to wallet {}", bankAccount.getAccountNumber(),
					walletId, e);
			return new ResponseEntity<>("Failed to link bank account. Please try again later.",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}