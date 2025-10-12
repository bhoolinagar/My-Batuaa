package com.myBatuaa.controller;

import com.myBatuaa.model.BankAccount;
import com.myBatuaa.repository.WalletRepository;
import com.myBatuaa.service.BankService;
import com.myBatuaa.service.WalletService;
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

@RestController
@RequestMapping("wallet/api/v1")
public class WalletController {

    @Autowired
   private WalletService walletService;
    @Autowired
   private BankService bankService;
    
    //BigDecimal getBalance( String walletId)
    @GetMapping("/get-wallet-balance/{walletId}")
    public ResponseEntity<?> getWalletBalance(@PathVariable String walletId){
        return new ResponseEntity<>(walletService.getWalletBalance(walletId),HttpStatus.OK);
    }

    //to link new bank account
    @PostMapping("/link-bank-account")
    public ResponseEntity<?> linkBankAccountToWallet(@RequestParam String walletId, @RequestBody BankAccount bankAccount){
        return new ResponseEntity<>(bankService.linkBankAccountToWallet(walletId,bankAccount),HttpStatus.CREATED);
    }


}