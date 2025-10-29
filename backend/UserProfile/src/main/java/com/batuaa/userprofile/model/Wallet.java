package com.batuaa.userprofile.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "wallet_records")
public class Wallet {
    @Id
    @Column(name = "wallet_id", nullable = false, unique = true)
    private String walletId;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "buyer_email", referencedColumnName = "emailId")// maps to User.userId
    private Buyer buyer;
    private LocalDateTime createdAt;
    private BigDecimal balance;
    private String bankName;
    @NotNull
    private String accountNumber;
    private Boolean isPrimary=false;
    public Boolean getPrimary() {
        return isPrimary;
    }
    public void setPrimary(Boolean primary) {
        isPrimary = primary;
    }
    public String getWalletId() {
        return walletId;
    }
    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }
    public Buyer getBuyer() {
        return buyer;
    }
    public void setBuyer(Buyer buyer) {
        this.buyer = buyer;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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
    public String getAccountNumber() {
        return accountNumber;
    }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

}