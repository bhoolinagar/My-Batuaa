package com.mybatuaa.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mybatuaa.model.Status;
import com.mybatuaa.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

	@Query("SELECT t FROM Transaction t "
			+ "WHERE (t.fromWallet.walletId = :walletId OR t.toWallet.walletId = :walletId) "
			+ "AND LOWER(t.remarks) LIKE LOWER(CONCAT('%', :keyword, '%'))")
	List<Transaction> findByWalletAndRemarkContainingIgnoreCase(@Param("walletId") String walletId,
			@Param("keyword") String remark);

	// find wallet id by custom range
	List<Transaction> findByFromWallet_WalletIdOrToWallet_WalletIdAndTimestampBetween(String walletId, String walletId1,
			LocalDateTime startOfDay, LocalDateTime endOfDay);

	List<Transaction> findByFromWallet_WalletIdAndStatus(String walletId, Status status);

    List<Transaction> findByFromWallet_WalletIdOrToWallet_WalletId(String fromwalletId,String towalletId);

}
