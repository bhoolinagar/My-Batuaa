package com.myBatuaa.service;

import com.myBatuaa.exception.BankAccountAlreadyLinkedException;
import com.myBatuaa.exception.WalletNotFoundException;
import com.myBatuaa.model.BankAccount;
import com.myBatuaa.model.Wallet;

import com.myBatuaa.repository.BankAccountResposity;
import com.myBatuaa.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BankServiceImpl  implements BankService{

    @Autowired
   private BankAccountResposity bankAccountRepository;
    @Autowired
    private WalletRepository walletRepository;

    @Override
    public BankAccount linkBankAccountToWallet(String walletId, BankAccount account) {
        // Step 1: Find wallet
        Wallet wallet = (Wallet) walletRepository.findByWalletId(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found with ID: " + walletId));

        // Step 2: Check if this wallet already has a linked bank account with same number
        boolean accountExists = bankAccountRepository.existsByAccountNumberAndWallet_WalletId(
                account.getAccountNumber(), walletId);

        if (accountExists) {
            throw new BankAccountAlreadyLinkedException(
                    "Bank account " + account.getAccountNumber() + " already linked to this wallet");
        }

        // Step 3: Link account to wallet
        account.setWallet(wallet);

     // Step 4: Add this account to wallet's existing list of accounts
        List<BankAccount> accounts = wallet.getBankAccounts();
        if (accounts == null) {
            accounts = new ArrayList<>();
        }
        accounts.add(account);
        wallet.setBankAccounts(accounts);

        // Step 5: Save wallet (optional if cascade is set) and bank account
        walletRepository.save(wallet);
        // Step 6: Save and return the linked bank account
        return bankAccountRepository.save(account);
    }


}