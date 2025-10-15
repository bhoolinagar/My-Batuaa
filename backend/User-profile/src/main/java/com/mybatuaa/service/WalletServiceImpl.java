package com.mybatuaa.service;

import com.mybatuaa.exception.WalletNotFoundException;
import com.mybatuaa.model.Wallet;
import com.mybatuaa.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Service
public class WalletServiceImpl implements WalletService {
    private final WalletRepository walletRepository;

    @Autowired
    public WalletServiceImpl(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

	// to generate walletId unique 11 characters with help of( userId,email,
	// phoneNumber)
	@Override
	public String generateWalletId(String email, String phoneNumber) {
		try {
			// Combine key user attributes for uniqueness
			String input = email + phoneNumber;

			// Create SHA-256 hash
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

			// Convert first few bytes to hex (for 8 hash characters)
			StringBuilder hex = new StringBuilder();
			for (int i = 0; i < 4; i++) { // 4 bytes = 8 hex chars
				hex.append(String.format("%02x", hash[i]));
			}

			// Build final ID: "WAL" + 8-character hash = 11 characters
            String s = "WAL" + hex.substring(0, 8).toUpperCase();
            return s;

		} catch (Exception e) {
			throw new RuntimeException("Error generating wallet ID", e);
		}
	}

	// to get balance from wallet

	@Override
	public BigDecimal getWalletBalance(String walletId) {
		return walletRepository.findByWalletId(walletId).map(Wallet::getBalance)
                .orElseThrow(() -> new WalletNotFoundException(STR."Wallet not found with ID: \{walletId}"));
	}

}