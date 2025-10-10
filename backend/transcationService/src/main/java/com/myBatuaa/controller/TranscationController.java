package com.myBatuaa.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("transcation/api/v1")
public class TranscationController {

//getAllTransactions(String walletId)
@GetMapping("/all-transcations")
public ResponseEntity<?> getMethodName(@RequestParam String WalletId) {
    return null;
}

	//addMoneyFromBank( String walletId, accountNumber, BigDecimal Amount)
@PostMapping("/add-money-from-bank")
public 	ResponseEntity<?>addMoneyFromBank(String walletId, Integer accountNumber, BigDecimal amount){
	return null;
}
	//TransferWalletToWallet(walletIdFrom, walletIdTo, BigDecimal Amount)
 @PostMapping("/transfer-wallet-to-wallet")
public ResponseEntity<?>transferMoneywalletToWallet(String walletIdFrom, String walletIdTo,BigDecimal amount) {
    //TODO: process POST request
      return null;
}

//Free bracket calculator-chargeable(amount)
 

	//viewTransactionByRemark (based on remark, transactionId)
  @GetMapping("/view-transactions-by-remark")
 public ResponseEntity<?>viewTransactionByRemark(@RequestParam String remark, @RequestParam Integer transactionId){
	return null; 
 }
	 //viewTransactionByType (based on type)
 @GetMapping("/view-transactions-by-type/{type}")
  public ResponseEntity<?>viewTransactionByTypes(String type){
		return null; 
	 }
	
	//viewHistoryByDate(based on date)
 @GetMapping("/view-transactions-by-remark/{type}")
 public ResponseEntity<?>viewTransactionByDate(LocalDateTime date){
		return null; 
	 }
	
	
	//sortingByAmount(based on amount)
 @GetMapping("/view-transactions-by-remark/{type}")
 public ResponseEntity<?>viewTransactionByAmount(BigDecimal amount){
		return null; 
	 }
	
}
