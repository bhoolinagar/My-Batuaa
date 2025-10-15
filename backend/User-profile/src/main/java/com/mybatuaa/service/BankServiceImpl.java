package com.mybatuaa.service;

import com.mybatuaa.exception.BankAccountAlreadyLinkedException;
import com.mybatuaa.exception.WalletNotFoundException;
import com.mybatuaa.model.BankAccount;
import com.mybatuaa.model.Wallet;
import com.mybatuaa.repository.BankAccountRepository;
import com.mybatuaa.repository.WalletRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class BankServiceImpl  implements BankService{

    @Autowired
    private BankAccountRepository bankAccountRepository;
    @Autowired
    private WalletRepository walletRepository;


    @Override
    public BankAccount linkBankAccountToWallet(String walletId, BankAccount account) {
        try {
            log.info("Attempting to link bank account {} to wallet {}", account.getAccountNumber(), walletId);

            // Step 1: Find wallet
            Wallet wallet = walletRepository.findByWalletId(walletId)
                    .orElseThrow(() -> new WalletNotFoundException("Wallet not found with ID: " + walletId));

            // Step 2: Check if this wallet already has the bank account
            boolean accountExists = bankAccountRepository.existsByAccountNumberAndWallet_WalletId(
                    account.getAccountNumber(), walletId);

            if (accountExists) {
                log.warn("Bank account {} is already linked to wallet {}", account.getAccountNumber(), walletId);
                throw new BankAccountAlreadyLinkedException(
                        "Bank account "+account.getAccountNumber()+" already linked to this wallet");

            }

            // Step 3: Link account to wallet
            account.setWallet(wallet);

            // Step 4: Add to wallet's existing list of accounts
            List<BankAccount> accounts = wallet.getBankAccounts();
            if (accounts == null) {
                accounts = new ArrayList<>();
            }
            accounts.add(account);
            wallet.setBankAccounts(accounts);

            // Step 5: Save wallet (optional if cascade is set) and bank account
            walletRepository.save(wallet);
            BankAccount savedAccount = bankAccountRepository.save(account);

            log.info("Bank account {} successfully linked to wallet {}", savedAccount.getAccountNumber(), walletId);
            return savedAccount;

        } catch (WalletNotFoundException | BankAccountAlreadyLinkedException e) {
            log.error("Error linking bank account {} to wallet {}: {}", account.getAccountNumber(), walletId, e.getMessage());
            throw e; // rethrow custom exceptions to be handled at controller layer
        } catch (Exception e) {
            log.error("Unexpected error linking bank account {} to wallet {}", account.getAccountNumber(), walletId, e);
            throw new RuntimeException("Failed to link bank account to wallet. Please try again later.", e);
        }
    }



}