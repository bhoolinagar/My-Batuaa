package com.myBatuaa.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    private String accountNumber;   // PK (usually string)

    private Integer bankId;         // Internal Bank ID (optional, not PK)

    private String bankName;

    private String ifscCode;        // IFSC is alphanumeric, so String

    private BigDecimal  balance;

}
