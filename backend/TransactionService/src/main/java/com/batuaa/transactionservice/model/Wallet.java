package com.batuaa.transactionservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "wallet_records")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "walletId")
public class Wallet {

    @Id
    @Column(name = "wallet_id", nullable = false, unique = true)
    private String walletId;
   // @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "buyer_email", referencedColumnName = "email_id")// maps to User.userId
    private Buyer buyer;
    private LocalDateTime createdAt;
    private BigDecimal balance;
    private String bankName;
    @NotNull
    private String accountNumber;

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


    public Wallet() {
    }

    public Wallet(String walletId) {
        this.walletId = walletId;
    }

}