package com.mybatuaa.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="wallet_records")
public class Wallet {

	@Id
	@Column(name="wallet_id",nullable = false, unique = true)
	private String walletId;
	//   @Column(name = "user_id")
	// private Integer userId;
	@OneToOne
	@JoinColumn(name = "user_id") // maps to User.userId
	private User user;

	private LocalDateTime createdAt;
	private BigDecimal balance;

	@OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL)
    @JsonManagedReference
	private List<BankAccount> bankAccounts;
	// One wallet → many sent transactions
	@OneToMany(mappedBy = "fromWallet", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
	private List<Transaction> sentTransactions = new ArrayList<>();

	//One wallet → many received transactions
	@OneToMany(mappedBy = "toWallet", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
	private List<Transaction> receivedTransactions = new ArrayList<>();

	//Combine both sent + received transactions
	@Transient
	public List<Transaction> getAllTransactions() {
		List<Transaction> all = new ArrayList<>();
		if (sentTransactions != null) {
			all.addAll(sentTransactions);
		}
		if (receivedTransactions != null) {
			all.addAll(receivedTransactions);
		}
		return all;
	}

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public List<BankAccount> getBankAccounts() {
        return bankAccounts;
    }

    public void setBankAccounts(List<BankAccount> bankAccounts) {
        this.bankAccounts = bankAccounts;
    }

    public List<Transaction> getSentTransactions() {
        return sentTransactions;
    }

    public void setSentTransactions(List<Transaction> sentTransactions) {
        this.sentTransactions = sentTransactions;
    }

    public List<Transaction> getReceivedTransactions() {
        return receivedTransactions;
    }

    public void setReceivedTransactions(List<Transaction> receivedTransactions) {
        this.receivedTransactions = receivedTransactions;
    }

    public Wallet(String walletId, User user, LocalDateTime createdAt, BigDecimal balance, List<BankAccount> bankAccounts, List<Transaction> sentTransactions, List<Transaction> receivedTransactions) {
        this.walletId = walletId;
        this.user = user;
        this.createdAt = createdAt;
        this.balance = balance;
        this.bankAccounts = bankAccounts;
        this.sentTransactions = sentTransactions;
        this.receivedTransactions = receivedTransactions;
    }

   public Wallet(){}
}