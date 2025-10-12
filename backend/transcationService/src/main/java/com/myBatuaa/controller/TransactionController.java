package com.myBatuaa.controller;

import com.myBatuaa.model.Transaction;
import com.myBatuaa.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("transaction/api/v1")
public class TransactionController {
	
	//@Autowired
    private final TransactionService transactionService;

	
    @Autowired
    public TransactionController(TransactionService transactionService){
        this.transactionService = transactionService;
    }

    //getAllTransactions(String walletId)
    @GetMapping("/all-transactions")
    public ResponseEntity<?> getMethodName(@RequestParam String WalletId) {
    return null;
}

    /*
    addMoneyFromBank( String walletIdto, accountNumber, BigDecimal Amount)
    */
    @PostMapping("/add-money-from-bank")
    public 	ResponseEntity<?>addMoneyFromBank(@RequestParam String walletIdto, @RequestParam String accountNumber, @RequestParam BigDecimal amount){
        Transaction transaction = transactionService.addMoney(walletIdto, amount);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);

    }
	/*
	TransferWalletToWallet(walletIdFrom, walletIdTo, BigDecimal Amount)
	 */
    @PostMapping("/transfer-wallet-to-wallet")
    public ResponseEntity<?>transferMoneywalletToWallet(@RequestParam String walletIdFrom, @RequestParam String walletIdTo,@RequestParam BigDecimal amount) {
        Transaction transaction = transactionService.transferWalletToWallet(walletIdFrom,walletIdTo,amount);
        return new ResponseEntity<>(transaction,HttpStatus.CREATED);
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
    @GetMapping("/view-transactions-by-date/{type}")
    public ResponseEntity<?>viewTransactionByDate(LocalDateTime date){
		return null; 
    }
	
	
	//sortingByAmount(based on amount)
    @GetMapping("/view-transactions-by-amount/{type}")
    public ResponseEntity<?>viewTransactionByAmount(BigDecimal amount){
        return null;
	 }
	
}
