package com.mybatuaa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mybatuaa.model.Wallet;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, String> {

	Optional<Wallet> findByWalletId(String walletId);

}