package com.mybatuaa.service;

import com.mybatuaa.model.BankAccount;


public interface BankService {
   // public BankAccount addBankAccountToWallet(String accountNumber, BankAccount bankAccount);


    //to add new bank account
    BankAccount linkBankAccountToWallet(String walletId, BankAccount account);
}

