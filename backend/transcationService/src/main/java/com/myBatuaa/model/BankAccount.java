package com.myBatuaa.model;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="bank_accounts")

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BankAccount {

    @Id
    @Column(name="account_number")
    private String accountNumber;   // PK (usually string)

    private Integer bankId;         // Internal Bank ID (optional, not PK)

    private String bankName;

    private String ifscCode;        // IFSC is alphanumeric, so String

    private BigDecimal  balance;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

}