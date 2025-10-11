package com.myBatuaa.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="wallet_records")


public class Wallet {
    public Wallet(String walletId, Integer userId, User user, LocalDate createdAt, BigDecimal balance, List<BankAccount> bankAccounts) {
        WalletId = walletId;
        this.userId = userId;
        this.user = user;
        this.createdAt = createdAt;
        this.balance = balance;
        this.bankAccounts = bankAccounts;
    }

    public Wallet() {
    }

    @Id
    private String WalletId;
    private Integer userId;

    public String getWalletId() {
        return WalletId;
    }

    public void setWalletId(String walletId) {
        WalletId = walletId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public List<BankAccount> getBankAccounts() {
        return bankAccounts;
    }

    public void setBankAccounts(List<BankAccount> bankAccounts) {
        this.bankAccounts = bankAccounts;
    }

    @MapsId
    @JoinColumn(name = "user_id")
    private User user;
    private LocalDate createdAt;
    private BigDecimal balance;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL)
    private List<BankAccount> bankAccounts;
}