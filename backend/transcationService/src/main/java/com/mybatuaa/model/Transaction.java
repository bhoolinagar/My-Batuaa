package com.mybatuaa.model;

import java.math.BigDecimal;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "transaction_records") // corrected name for proper mapping with db.

public class Transaction {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer transactionId;  // Auto-generated PK

    @ManyToOne
    @JoinColumn(name = "account_number", referencedColumnName = "account_number")
    @JsonBackReference
    private BankAccount account;    // FK to BankAccount

    @ManyToOne
    @JoinColumn(name = "from_wallet_id", referencedColumnName = "wallet_id")
    @JsonBackReference
    private Wallet fromWallet;      // FK to source Wallet

    @ManyToOne
    @JoinColumn(name = "to_wallet_id", referencedColumnName = "wallet_id")
    @JsonBackReference
    private Wallet toWallet;        // FK to destination Wallet

    private BigDecimal amount;

    private LocalDateTime timestamp; // Full date & time

    @Enumerated(EnumType.STRING)
    private Status status; // SUCCESS, PROCESSING, FAILED

    private String remarks;

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public BankAccount getAccount() {
        return account;
    }

    public void setAccount(BankAccount account) {
        this.account = account;
    }

    public Wallet getFromWallet() {
        return fromWallet;
    }

    public void setFromWallet(Wallet fromWallet) {
        this.fromWallet = fromWallet;
    }

    public Wallet getToWallet() {
        return toWallet;
    }

    public void setToWallet(Wallet toWallet) {
        this.toWallet = toWallet;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Transaction(Integer transactionId, BankAccount account, Wallet fromWallet, Wallet toWallet, BigDecimal amount, LocalDateTime timestamp, Status status, String remarks) {
        this.transactionId = transactionId;
        this.account = account;
        this.fromWallet = fromWallet;
        this.toWallet = toWallet;
        this.amount = amount;
        this.timestamp = timestamp;
        this.status = status;
        this.remarks = remarks;
    }
    public Transaction(){

    }
}


