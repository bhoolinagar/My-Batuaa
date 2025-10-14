package com.mybatuaa.model;

import java.math.BigDecimal;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="bank_accounts")

public class BankAccount {

	@Id
    @Column(name = "account_number", nullable = false, unique = true)
	private String accountNumber; // PK (usually string)
	@NotNull
	private Integer bankId; // Internal Bank ID (optional, not PK)
	@NotNull
	private String bankName;
	@NotNull
	private String ifscCode; // IFSC is alphanumeric, so String
	@NotNull
	private BigDecimal balance;

	@ManyToOne
	@JoinColumn(name = "wallet_id")
	private Wallet wallet;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Integer getBankId() {
        return bankId;
    }

    public void setBankId(Integer bankId) {
        this.bankId = bankId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public BankAccount(String accountNumber, Integer bankId, String bankName, String ifscCode, BigDecimal balance, Wallet wallet) {
        this.accountNumber = accountNumber;
        this.bankId = bankId;
        this.bankName = bankName;
        this.ifscCode = ifscCode;
        this.balance = balance;
        this.wallet = wallet;
    }
    public BankAccount(){}
}