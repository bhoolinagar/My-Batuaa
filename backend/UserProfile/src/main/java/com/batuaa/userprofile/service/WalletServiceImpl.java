package com.batuaa.userprofile.service;
import com.batuaa.userprofile.dto.WalletDto;
import com.batuaa.userprofile.exception.BuyerNotFoundException;
import com.batuaa.userprofile.exception.WalletAlreadyFound;
import com.batuaa.userprofile.exception.WalletNotFoundException;
import com.batuaa.userprofile.model.Buyer;
import com.batuaa.userprofile.model.Wallet;
import com.batuaa.userprofile.repository.BuyerRepository;
import com.batuaa.userprofile.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class WalletServiceImpl implements WalletService {
    @Autowired
    private final WalletRepository walletRepository;
    @Autowired
    private final BuyerRepository buyerRepository;

    @Autowired
    public WalletServiceImpl(WalletRepository walletRepository, BuyerRepository buyerRepository) {
        this.walletRepository = walletRepository;
        this.buyerRepository = buyerRepository;
    }

    // to generate walletId unique 11 characters with help of( userId,email,
    // accountNumber)
    @Override
    public String generateWalletId(String email, String accountNumber) {
        try {
            // Step 1: Combine normalized  email , account number ,nanoTime for uniqueness
            String input = (email.trim().toLowerCase() + accountNumber.trim()) + System.nanoTime();

            // Step 2: Create SHA-256 hash
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            // Step 3: Convert first 6 bytes to hex (12 hex chars)
            StringBuilder hex = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                hex.append(String.format("%02x", hash[i]));
            }

            // Step 4: Build final ID — "WAL" + 9 chars = 12 total
            String walletId = "WAL" + hex.substring(0, 9).toUpperCase();

            return walletId;

        } catch (NoSuchAlgorithmException e) {
            // Handle if SHA-256 algorithm not found
            System.err.println("Error: SHA-256 algorithm unavailable. " + e.getMessage());
            return "WAL" + System.currentTimeMillis() % 1000000000L; // Fallback (12 digits)
        } catch (Exception e) {
            // Catch all unexpected errors
            System.err.println("Unexpected error generating wallet ID: " + e.getMessage());
            return "WAL" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 9).toUpperCase();
        }
    }


    // to get balance from wallet

    @Transactional
    @Override
    public String linkBankAccountToWallet(WalletDto wallet) {
        try {
            log.info("Starting wallet linking process for email: {}", wallet.getEmailId());

            // Step 1: Generate unique walletId
            String walletId = generateWalletId(wallet.getEmailId(), wallet.getAccountNumber());
            int retryCount = 0;

            while (retryCount < 10 && walletRepository.existsByWalletId(walletId)) {
                log.warn("Duplicate walletId detected ({}), regenerating...", walletId);
                walletId = generateWalletId(wallet.getEmailId(), wallet.getAccountNumber());
                retryCount++;
            }

            // Step 2: Check if account already linked
            if (walletRepository.existsByBuyerEmailIdAndAccountNumber(wallet.getEmailId(), wallet.getAccountNumber())) {
                log.error("Bank account already linked for email: {}", wallet.getEmailId());
                throw new WalletAlreadyFound("Bank account already linked with a wallet");
            }

            // Step 3: Fetch buyer
            Buyer buyer = buyerRepository.findByEmailId(wallet.getEmailId())
                    .orElseThrow(() -> new BuyerNotFoundException("Buyer not registered with this email ID"));
            // Step 4: Create wallet object
            Wallet walletObj = new Wallet();
            walletObj.setWalletId(walletId);
            walletObj.setAccountNumber(wallet.getAccountNumber());
            walletObj.setBankName(wallet.getBankName());
            walletObj.setBalance(wallet.getBalance());
            walletObj.setBuyer(buyer);
            walletObj.setCreatedAt(LocalDateTime.now());

            // Step 5: Save wallet
            walletRepository.save(walletObj);
            log.info("Wallet successfully linked with ID: {}", walletId);

            return "Bank account linked successfully with wallet ID: " + walletId;

        } catch (WalletAlreadyFound e) {
            log.error("WalletAlreadyFound Exception: {}", e.getMessage());
            throw e; // Re-throw to trigger rollback

        } catch (BuyerNotFoundException e) {
            log.error("BuyerNotFound Exception: {}", e.getMessage());
            throw e; // Re-throw to trigger rollback

        } catch (Exception e) {
            log.error("Unexpected error while linking wallet for email {}: {}", wallet.getEmailId(), e.getMessage(), e);
            throw new RuntimeException("Failed to link bank account to wallet. Please try again later.", e);
        }
    }


    // to add more money from bank to wallet
    @Override
    @Transactional
    public String updateMoneyFromBank(String walletId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        try {
            // Step 1: Fetch wallet or throw exception
            Wallet wallet = walletRepository.findByWalletId(walletId)
                    .orElseThrow(() -> new WalletNotFoundException("Wallet not found with ID: " + walletId));

            // Step 2: Update balance
            BigDecimal newBalance = wallet.getBalance().add(amount);
            wallet.setBalance(newBalance);

            // Step 3: Save wallet
            walletRepository.save(wallet);
            return "Money successfully added into wallet. New Balance: " + newBalance;

        } catch (WalletNotFoundException e) {
            throw e; // propagate known exception

        } catch (Exception e) {
            throw new RuntimeException("Failed to add money to wallet. Please try again.", e);
        }
    }

    @Override
    public Wallet getWalletDetails(String email, String walletId) {
        try {
            return walletRepository.findByBuyerEmailIdAndWalletId(email, walletId)
                    .orElseThrow(() -> new WalletNotFoundException(
                            "Wallet not found with ID: " + walletId
                    ));
        } catch (WalletNotFoundException ex) {
            // You can log the error and rethrow or return null depending on your requirement
            log.error("Error fetching wallet: {}", ex.getMessage());
            // Option 1: Rethrow as runtime exception
            throw ex;
            // Option 2: Return null instead of throwing
            // return null;
        } catch (Exception ex) {
            log.error("Unexpected error fetching wallet: {}", ex.getMessage());
            throw new RuntimeException("Failed to fetch wallet details", ex);
        }
    }

    @Override
    public List<Wallet> getWalletListByBuyer(String email) {
        Buyer buyer = buyerRepository.findByEmailId(email)
                .orElseThrow(() -> new BuyerNotFoundException("Buyer not found"));

        List<Wallet> wallets = walletRepository.findAllByBuyer(buyer);
        if (wallets.isEmpty()) {
            throw new WalletNotFoundException("No wallets found for this buyer");
        }
        return wallets;
    }

}