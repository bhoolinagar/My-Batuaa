package com.mybatuaa.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Setter
@Getter
@Entity
@Table(name = "wallet_records")
public class Wallet {

	@Id
	@Column(name = "wallet_id", nullable = false, unique = true)
	private String walletId;

	@OneToOne
	@JoinColumn(name = "user_id") // maps to User.userId
	private User user;

	private LocalDateTime createdAt;
	private BigDecimal balance;
    @JsonBackReference
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