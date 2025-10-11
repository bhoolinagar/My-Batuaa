package com.myBatuaa.service;

import com.myBatuaa.exception.InsufficientFundsException;
import com.myBatuaa.exception.WalletNotFoundException;
import com.myBatuaa.model.Status;
import com.myBatuaa.model.Transaction;
import com.myBatuaa.model.Wallet;
import com.myBatuaa.repository.TransactionRepository;
import com.myBatuaa.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TransactionServiceImp implements TransactionService{

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    @Autowired
    public TransactionServiceImp(TransactionRepository transactionRepository, WalletRepository walletRepository){
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
    }
    /*
       Functional Purpose:  Transaction Getting Recorded, Once Money Is Added To The Wallet
       Step 1: Validate that the wallet exists and throwing exception if wallets not found
       Step 2: Transaction object creation | Currently tight coupling, post MVP loose coupling logic will be handled
       (Status to be handled using try catch logic once the integrated logic is checked)
       Step 3: Saving the transaction
    (Transactional will ensure atomicity: if any exception will occur, both wallets and transaction rollback.)
     */

    @Transactional
    @Override
    public Transaction addMoney(String walletId, BigDecimal amount) {
        Wallet wallet = walletRepository
                .findByWalletId(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found."));
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setStatus(Status.SUCCESS);
        transaction.setRemarks("Money added to wallet " + walletId);
        return transactionRepository.save(transaction);
    }

    /*
       Functional Purpose: Transaction created
       Step 1: Fetching wallets and throwing exception if wallets not found.
       Step 2: Validating balance for Sufficient Funds.
       Step 3: Updating balance and Save the updated amount to wallets.
       Step 4: Creation of a transaction for both wallets recorder.

     */
    @Transactional
    @Override
    public Transaction transferWalletToWallet(String walletIdFrom, String walletIdTo, BigDecimal amount) {
        Wallet walletFrom = walletRepository.findByWalletId(walletIdFrom)
                .orElseThrow(() -> new WalletNotFoundException("Sender wallet not found: "+ walletIdFrom));
        Wallet walletTo  = walletRepository.findByWalletId(walletIdTo)
                .orElseThrow(() -> new WalletNotFoundException("Recipient wallet not found"+ walletIdTo));
        if (walletFrom.getBalance().compareTo(amount) <= 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }
        walletFrom.setBalance(walletFrom.getBalance().subtract(amount));
        walletTo.setBalance(walletTo.getBalance().add(amount));

        walletRepository.save(walletFrom);
        walletRepository.save(walletTo);

//      Sender
        Transaction senderTransaction = new Transaction();
        senderTransaction.setAmount(amount);
        senderTransaction.setTimestamp(LocalDateTime.now());
        senderTransaction.setStatus(Status.SUCCESS); // To be handled via try catch
        senderTransaction.setRemarks("Transferred Rs" + amount + " to wallet " + walletIdTo);
        transactionRepository.save(senderTransaction);
//      Receiver
        Transaction receiverTransaction = new Transaction();
        receiverTransaction.setAmount(amount);
        receiverTransaction.setTimestamp(LocalDateTime.now());
        receiverTransaction.setStatus(Status.SUCCESS); // To be handled via try catch
        receiverTransaction.setRemarks("Received Rs" + amount + " from wallet " + walletIdFrom);
        transactionRepository.save(receiverTransaction);

        return  senderTransaction;
    }
}
