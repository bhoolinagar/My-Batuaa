package com.mybatuaa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mybatuaa.model.BankAccount;

@Repository
public interface BankAccountResposity extends JpaRepository<BankAccount, String> {
	boolean existsByWallet_WalletId(String walletId);

	boolean existsByAccountNumberAndWallet_WalletId(String accountNumber, String walletId);
}
