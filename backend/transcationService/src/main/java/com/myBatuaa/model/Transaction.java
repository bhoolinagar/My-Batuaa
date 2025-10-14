package com.myBatuaa.model;

import java.math.BigDecimal;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "transaction_records") // corrected name for proper mapping with db.
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer transactionId;  // Auto-generated PK

    @ManyToOne
    @JoinColumn(name = "account_number", referencedColumnName = "account_number")
    private BankAccount account;    // FK to BankAccount

    @ManyToOne
    @JoinColumn(name = "from_wallet_id", referencedColumnName = "wallet_id")
    private Wallet fromWallet;      // FK to source Wallet

    @ManyToOne
    @JoinColumn(name = "to_wallet_id", referencedColumnName = "wallet_id")
    private Wallet toWallet;        // FK to destination Wallet

    private BigDecimal amount;

    private LocalDateTime timestamp; // Full date & time

    @Enumerated(EnumType.STRING)
    private Status status; // SUCCESS, PROCESSING, FAILED

    private String remarks;
}
