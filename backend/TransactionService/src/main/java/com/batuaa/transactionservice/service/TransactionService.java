package com.batuaa.transactionservice.service;

import com.batuaa.transactionservice.dto.TransactionDateRangeDto;
import com.batuaa.transactionservice.dto.TransactionRemarkDto;
import com.batuaa.transactionservice.dto.TransactionTypeDto;
import com.batuaa.transactionservice.dto.TransferDto;
import com.batuaa.transactionservice.exception.InvalidEmailAddressException;
import com.batuaa.transactionservice.model.Role;
import com.batuaa.transactionservice.model.Transaction;

import java.util.List;

public interface TransactionService {

    // Transfer money from one wallet to another
    Transaction transferWalletToWallet(TransferDto transferDto) throws InvalidEmailAddressException;

    // Find transactions by walletId within a specific date range
    List<Transaction> findByWalletIdAndDateBetween(TransactionDateRangeDto transactionDateRangeDto);

    // View transactions filtered by type (DEBIT or CREDIT)
    List<Transaction> viewTransactionsByType(TransactionTypeDto transactionTypeDto);

    // Filter transactions by remarks
    List<Transaction> filterTransactionsByRemark(TransactionRemarkDto transactionRemarkDto);

    // Get all transactions for a given wallet and email, sorted by recent date
    List<Transaction> getAllTransactions(String emailId, String walletId);

    // Sort transactions by amount (ascending or descending)
    List<Transaction> sortTransactionsByAmount(String walletId, String emailId, String sortOrder);
    // Get a single transaction by walletId and email
    Transaction getTransactionByWalletId(String emailId, String walletId);

    List<Transaction> findByAdminEmailAndRole(String email, Role role);
}

