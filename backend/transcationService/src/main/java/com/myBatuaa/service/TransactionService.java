package com.myBatuaa.service;

import com.myBatuaa.model.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {
    //    Add Money to Wallet
    Transaction addMoney(String walletId, String accountNumber, BigDecimal amount);
    //    Transfer money wallet-to-wallet
    Transaction transferWalletToWallet(String walletIdFrom, String walletIdTo, BigDecimal amount);
    //   Filter Transactions By Remarks
//    List<Transaction> filterTransactionsByRemark(String walletId, String remark);

}
