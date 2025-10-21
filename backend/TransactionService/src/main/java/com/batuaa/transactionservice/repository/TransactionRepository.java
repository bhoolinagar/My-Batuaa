package com.batuaa.transactionservice.repository;


import com.batuaa.transactionservice.model.Status;
import com.batuaa.transactionservice.model.Transaction;
import com.batuaa.transactionservice.model.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    //find wallet id by custom range
    List<Transaction> findByFromWallet_WalletIdOrToWallet_WalletIdAndTimestampBetween(String walletId, String walletId1, LocalDateTime startOfDay, LocalDateTime endOfDay);


    // List<Transaction>findByFromWallet_WalletIdOrToWallet_WalletId(String )
    //List<Transaction> findByFromWallet_WalletIdAndType(String walletId, Type type);

    @Query("""
        SELECT t FROM Transaction t
        WHERE (UPPER(t.fromWallet.walletId) = UPPER(:walletId)
               OR UPPER(t.toWallet.walletId) = UPPER(:walletId))
          AND t.timestamp BETWEEN :startTime AND :endTime
        ORDER BY t.timestamp DESC
    """)
    Page<Transaction> findByWalletAndTimestampRange(
            @Param("walletId") String walletId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable
    );

    @Query("""
            select t from Transaction t
            where t.fromWallet.walletId = :walletId and t.toWallet.walletId = :walletId1 and t.type = :type""")
    List<Transaction> findByWalletIdAndType(@Param("walletId") String walletId, @Param("walletId1") String walletId1, @Param("type") Type type);

    @Query(value = "SELECT * FROM transaction_records " +
            "WHERE from_email_id = ?1 " +
            "AND (from_wallet_id = ?2 OR to_wallet_id = ?2)",
            nativeQuery = true)
    List<Transaction> findByEmailAndWallet(String emailId, String walletId);



}
