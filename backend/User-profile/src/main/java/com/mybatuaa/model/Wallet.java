package com.mybatuaa.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@Entity
@Table(name = "wallet_records")
public class Wallet {

	@Id
	@Column(name = "wallet_id", nullable = false, unique = true)
	private String walletId;
	// @Column(name = "user_id")
	// private Integer userId;
	@OneToOne
	@JoinColumn(name = "user_id") // maps to User.userId
	private User user;

	private LocalDateTime createdAt;
	private BigDecimal balance;

	@OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL)
	private List<BankAccount> bankAccounts;

    public Wallet(){}
    public Wallet(String walletId, User user, LocalDateTime createdAt, BigDecimal balance, List<BankAccount> bankAccounts) {
        this.walletId = walletId;
        this.user = user;
        this.createdAt = createdAt;
        this.balance = balance;
        this.bankAccounts = bankAccounts;
    }
}