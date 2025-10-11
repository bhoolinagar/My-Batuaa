package com.myBatuaa.service;

import com.myBatuaa.model.BankAccount;

import com.myBatuaa.repository.BankAccountResposity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


public interface BankService {
   // public BankAccount addBankAccountToWallet(String accountNumber, BankAccount bankAccount);


    //to add new bank account
    BankAccount linkBankAccountToWallet(String walletId, BankAccount account);
}

