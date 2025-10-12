package com.myBatuaa.service;

import com.myBatuaa.exception.BankNotFoundException;
import com.myBatuaa.exception.InsufficientFundsException;
import com.myBatuaa.exception.WalletNotFoundException;
import com.myBatuaa.model.BankAccount;
import com.myBatuaa.model.Status;
import com.myBatuaa.model.Transaction;
import com.myBatuaa.model.Wallet;
import com.myBatuaa.repository.BankAccountResposity;
import com.myBatuaa.repository.TransactionRepository;
import com.myBatuaa.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TransactionServiceImp implements TransactionService {

//	@Autowired
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
//  @Autowired
    private final BankAccountResposity bankAccountResposity;


    @Autowired
    public TransactionServiceImp(TransactionRepository transactionRepository, WalletRepository walletRepository,
                                 BankAccountResposity bankAccountResposity){
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.bankAccountResposity = bankAccountResposity;
    }
    /*
       Functional Purpose:  Transaction Getting Recorded, Once Money Is Added To The Wallet
       Transaction object creation | Currently tight coupling, post MVP loose coupling logic will be handled
       (Status to be handled using try catch logic once the integrated logic is checked)
    (Transactional will ensure atomicity: if any exception will occur, both wallets and transaction rollback.)
     */

    @Transactional
    @Override
    public Transaction addMoney(String walletIdto, String accountNumber, BigDecimal amount) {

        BankAccount source = bankAccountResposity.findById(accountNumber)
                .orElseThrow(() -> new BankNotFoundException("Bank not found."));
        Wallet destination = source.getWallet();
        if (destination == null) {
            throw new WalletNotFoundException("Wallet not linked to bank account: " + accountNumber);
        }
        if (source.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }
        source.setBalance(source.getBalance().subtract(amount));
        bankAccountResposity.save(source);
        destination.setBalance(destination.getBalance().add(amount));
        walletRepository.save(destination);
        Transaction transaction = new Transaction();
        transaction.setAccount(source);
        transaction.setToWallet(destination);
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setStatus(Status.SUCCESS); // To be handled via try catch
        transaction.setRemarks("Money added to wallet " + walletIdto);
        return transactionRepository.save(transaction);
    }


    /*
     Functional Purpose: Transactions created when money Transferred between wallets
    */
    @Transactional
    @Override
    public Transaction transferWalletToWallet(String walletIdFrom, String walletIdTo, BigDecimal amount) {

        Wallet walletFrom = walletRepository.findByWalletId(walletIdFrom)
                .orElseThrow(() -> new WalletNotFoundException("Sender wallet not found: "+ walletIdFrom));

        Wallet walletTo  = walletRepository.findByWalletId(walletIdTo)
                .orElseThrow(() -> new WalletNotFoundException("Receiver wallet not found"+ walletIdTo));

        if (walletFrom.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }
        walletFrom.setBalance(walletFrom.getBalance().subtract(amount));
        walletTo.setBalance(walletTo.getBalance().add(amount));
        walletRepository.save(walletFrom);
        walletRepository.save(walletTo);
//      Sender
        Transaction senderTransaction = new Transaction();
        senderTransaction.setFromWallet(walletFrom);
        senderTransaction.setToWallet(walletTo);
        senderTransaction.setAmount(amount);
        senderTransaction.setTimestamp(LocalDateTime.now());
        senderTransaction.setStatus(Status.SUCCESS); // To be handled via try catch
        senderTransaction.setRemarks("Transferred Rs" + amount + " to wallet " + walletIdTo);
        transactionRepository.save(senderTransaction);
//      Receiver
        Transaction receiverTransaction = new Transaction();
        receiverTransaction.setFromWallet(walletFrom);
        receiverTransaction.setToWallet(walletTo);
        receiverTransaction.setAmount(amount);
        receiverTransaction.setTimestamp(LocalDateTime.now());
        receiverTransaction.setStatus(Status.SUCCESS); // To be handled via try catch
        receiverTransaction.setRemarks("Received Rs" + amount + " from wallet " + walletIdFrom);
        transactionRepository.save(receiverTransaction);

        return  senderTransaction;
    }
}
