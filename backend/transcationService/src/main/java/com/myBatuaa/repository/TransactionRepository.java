package com.myBatuaa.repository;

import com.myBatuaa.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    @Query("SELECT t FROM Transaction t " +
            "WHERE (t.fromWallet.walletId = :walletId OR t.toWallet.walletId = :walletId) " +
            "AND LOWER(t.remarks) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Transaction> findByWalletAndRemarkContainingIgnoreCase(@Param("walletId") String walletId,@Param("keyword") String remark);
}
