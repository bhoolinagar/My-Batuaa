package com.mybatuaa.service;

import com.mybatuaa.model.BankAccount;


public interface BankService {
    //to add new bank account
    BankAccount linkBankAccountToWallet(String walletId, BankAccount account);
}

