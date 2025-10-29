package com.batuaa.userprofile.repository;

import com.batuaa.userprofile.model.Buyer;
import com.batuaa.userprofile.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, String> {
    // Locks row in DB to prevent concurrent access, will prevents race conditions on same wallet
    // @Lock(LockModeType.PESSIMISTIC_WRITE)
    // Find wallet by buyer email and wallet ID
    Optional<Wallet> findByBuyerEmailIdAndWalletId(String email, String walletId);

    Optional<Wallet> findByWalletId(String walletId);

    boolean existsByWalletId(String walletId);

    boolean existsByBuyerEmailIdAndAccountNumber(String emailId, String accountNumber);

    List<Wallet> findAllByBuyer(Buyer buyer);

    List<Wallet> findByBuyerEmailId(String email);

    Optional<Wallet> findByBuyerEmailIdAndIsPrimaryTrue(String email);


}