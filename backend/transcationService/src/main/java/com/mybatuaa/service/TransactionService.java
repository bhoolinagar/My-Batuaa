package com.mybatuaa.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.mybatuaa.model.Status;
import com.mybatuaa.model.Transaction;

public interface TransactionService {
	// Add Money to Wallet
	Transaction addMoney(String walletId, String accountNumber, BigDecimal amount);

	// Transfer money wallet-to-wallet
	Transaction transferWalletToWallet(String walletIdFrom, String walletIdTo, BigDecimal amount);

	// Filter Transactions By Remarks
	List<Transaction> filterTransactionsByRemark(String walletId, String remark);

// View transactions by custom range
	List<Transaction> filterTransactionsByDateRange(String walletId, LocalDate startDate, LocalDate endDate);

	// View transaction by Type (based on type)
	List<Transaction> filterTransactionByType(String walletId, Status type);
}
