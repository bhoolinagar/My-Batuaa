package com.myBatuaa.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("wallet/api/v1")
public class WalletController {

	//BigDecimal getBalance( String walletId)
	@GetMapping("/get-balance/{walletId}")
	public ResponseEntity<?> getWalletBalance(String walletId){
		return null;
	}
	
	//to create wallet
	@PostMapping("/create-wallet")
	public ResponseEntity<?> createWallet(String walletId){
		return null;
	}	
	
	//to add new bank account 
	@PostMapping("/add-bank-account")
	public ResponseEntity<?> addBankAcounttoWallet(String walletId){
			return null;
		}	

	
}
