package com.myBatuaa.service;

import java.math.BigDecimal;

public interface WalletService {
    //to generate walletId unique 11 characters with help of( userId,email, phoneNumber)
    // Generates a unique 11-character wallet ID using email and phone number
    public String generateWalletId(String email, String phoneNumber);

    // to get balance from wallet
    public  BigDecimal getWalletBalance(String walletId);
}
