package com.batuaa.transactionservice.repository;


import com.batuaa.transactionservice.model.Status;
import com.batuaa.transactionservice.model.Transaction;
import com.batuaa.transactionservice.model.Type;
import com.batuaa.transactionservice.model.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    @Query("""
        SELECT t FROM Transaction t
        WHERE ((UPPER(t.fromWallet.walletId) = UPPER(:walletId) AND t.type='WITHDRAWN')
               OR (UPPER(t.toWallet.walletId) = UPPER(:walletId)) AND t.type='RECEIVED')
          AND t.timestamp BETWEEN :startTime AND :endTime
        ORDER BY t.timestamp DESC
    """)
    Page<Transaction> findByWalletAndTimestampRange(
            @Param("walletId") String walletId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable
    );

//for withrawl wallet id
    @Query("select t from Transaction t where t.fromWallet.walletId = ?1 and t.type = ?2")
    List<Transaction> findByFromWalletIdAndType(String walletId, Type type);
//for receiver wallet
    @Query("select t from Transaction t where t.toWallet.walletId = ?1 and t.type = ?2")
    List<Transaction> findByToWalletIdAndType(String walletId, Type type);

    @Query("""
            select t from Transaction t
            where t.fromWallet.walletId = :walletId and t.toWallet.walletId = :walletId1 and t.type = :type""")
    List<Transaction> findByWalletIdAndType(@Param("walletId") String walletId, @Param("walletId1") String walletId1, @Param("type") Type type);


//    @Query(
//            value = "SELECT * FROM transaction_records t " +
//                    "WHERE (UPPER(t.from_wallet_id) = UPPER(:walletId) OR UPPER(t.to_wallet_id) = UPPER(:walletId)) " +
//                    "AND (UPPER(t.from_email_id) = UPPER(:emailId) OR UPPER(t.to_email_id) = UPPER(:emailId)) " +
//                    "AND LOWER(t.remarks) LIKE LOWER(CONCAT('%', :keyword, '%'))",
//            nativeQuery = true)
@Query(value = """
    SELECT * FROM transaction_records t
    WHERE 
        (
            UPPER(t.from_wallet_id) = UPPER(:walletId)
            AND t.type = 'WITHDRAWN'
            AND LOWER(t.remarks) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
        OR
        (
            UPPER(t.to_wallet_id) = UPPER(:walletId)
            AND t.type = 'RECEIVED'
            AND LOWER(t.remarks) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
    """, nativeQuery = true)
    List<Transaction> findByWalletAndEmailAndRemarkNative(
            @Param("walletId") String walletId,
            @Param("emailId") String emailId,
            @Param("keyword") String keyword);


    Optional<Transaction> findByFromWalletAndToWalletAndAmountAndStatus(Wallet walletFrom, Wallet walletTo, BigDecimal amount, Status status);

  /*  @Query(value = "SELECT * FROM transaction_records " +
            "WHERE from_email_id = ?1 " +
            "AND (from_wallet_id = ?2 OR to_wallet_id = ?2)",
            nativeQuery = true)
    List<Transaction> findByEmailAndWallet(String emailId, String walletId);


   */


    @Query("SELECT t from Transaction t where (t.fromWallet.walletId = :walletId OR t.toWallet.walletId = :walletId)" +
            "AND (t.fromBuyer.emailId = :emailId OR t.toBuyer.emailId = :emailId)" +
            "ORDER BY t.amount")
    List<Transaction> findTransactionAmountByWalletAndEmail(@Param("walletId") String walletId, @Param("emailId") String emailId);

    @Query("""
SELECT t FROM Transaction t
WHERE 
    (
        UPPER(t.fromWallet.walletId) = UPPER(:walletId)
        AND UPPER(t.fromBuyer.emailId) = UPPER(:emailId)
        AND t.type = 'WITHDRAWN'
    )
    OR
    (
        UPPER(t.toWallet.walletId) = UPPER(:walletId)
        AND UPPER(t.toBuyer.emailId) = UPPER(:emailId)
        AND t.type = 'RECEIVED'
    )
""")
    List<Transaction> findByEmailAndWallet(@Param("emailId") String emailId, @Param("emailId1") String emailId1, @Param("walletId") String walletId, @Param("walletId1") String walletId1);

    List<Transaction>  findByFromEmailAndPrimaryWallet(String emailId,String walletId);

    @Query(value = "SELECT * FROM transaction_records " +
            "WHERE type = 'RECEIVED'" + "AND to_email_id = ?1 AND to_wallet_id = ?2",
            nativeQuery = true)
    List<Transaction>  findByToEmailAndToWallet(String emailId,String walletId);

    //    trasaction data for download
//@Query(value = "SELECT * FROM transaction_records", nativeQuery = true)
    @Query(value = """
        SELECT
                                                                                     t.transaction_id,
                                                                                     t.from_wallet_id,
                                                                                     t.to_wallet_id,
                                                                                     t.from_email_id,
                                                                                     t.to_email_id,
                                                                                     t.amount,
                                                                                     t.timestamp,
                                                                                     t.status,
                                                                                     t.remarks,
                                                                                     t.type
                                                                                 FROM transaction_records t
                                                                                 JOIN buyer_records fb ON t.from_email_id = fb.email_id
                                                                                 JOIN buyer_records tb ON t.to_email_id = tb.email_id
                                                                                 JOIN wallet_records fw ON t.from_wallet_id = fw.wallet_id
                                                                                 JOIN wallet_records tw ON t.to_wallet_id = tw.wallet_id;
""", nativeQuery = true)
    List<Transaction> findAllTransactionByAdminEmailAndRole();


}
