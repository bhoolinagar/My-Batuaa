package com.mybatuaa.repository;

import com.mybatuaa.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankAccountRepository  extends JpaRepository<BankAccount, String>{
	boolean existsByWallet_WalletId(String walletId);

    boolean existsByAccountNumberAndWallet_WalletId(String accountNumber, String walletId);
}

	

