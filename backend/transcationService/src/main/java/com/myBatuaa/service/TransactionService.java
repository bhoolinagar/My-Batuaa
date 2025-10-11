package com.myBatuaa.service;

import com.myBatuaa.model.Transaction;

import java.math.BigDecimal;

public interface TransactionService {
    //    Add Money to Wallet
    Transaction addMoney(String walletId, BigDecimal amount);
    //    Transfer money wallet-to-wallet
    Transaction transferWalletToWallet(String walletIdFrom, String walletIdTo, BigDecimal amount);

}
