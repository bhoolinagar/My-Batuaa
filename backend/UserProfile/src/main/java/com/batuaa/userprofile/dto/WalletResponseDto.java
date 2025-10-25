package com.batuaa.userprofile.dto;

import com.batuaa.userprofile.model.Wallet;

import java.math.BigDecimal;


public class WalletResponseDto {
    private String walletId;
    private String accountNumber;
    private BigDecimal balance;
    private String bankName;
    private String buyerEmail;
    private String buyerName;

    public WalletResponseDto() {
    }

    public WalletResponseDto(Wallet wallet) {
        this.walletId = wallet.getWalletId();
        this.accountNumber = wallet.getAccountNumber();
        this.balance = wallet.getBalance();
        this.bankName = wallet.getBankName();
        this.buyerEmail = wallet.getBuyer().getEmailId();
        this.buyerName = wallet.getBuyer().getName();
    }

    // getters & setters


    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBuyerEmail() {
        return buyerEmail;
    }

    public void setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }
}