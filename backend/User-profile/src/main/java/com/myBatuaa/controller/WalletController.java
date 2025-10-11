package com.myBatuaa.controller;

import com.myBatuaa.model.BankAccount;
import com.myBatuaa.repository.WalletRepository;
import com.myBatuaa.service.BankService;
import com.myBatuaa.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("wallet/api/v1")
public class WalletController {

    @Autowired
   private WalletService walletService;
    @Autowired
   private BankService bankService;
    //BigDecimal getBalance( String walletId)
    @GetMapping("/get-wallet-balance/{walletId}")
    public ResponseEntity<?> getWalletBalance(String walletId){
        return new ResponseEntity<>(walletService.getWalletBalance(walletId),HttpStatus.OK);
    }

    //to add new bank account
    @PostMapping("/add-bank-account")
    public ResponseEntity<?> linkBankAccountToWallet(String walletId, BankAccount bankAccount){
        return new ResponseEntity<>(bankService.linkBankAccountToWallet(walletId,bankAccount),HttpStatus.CREATED);
    }


}