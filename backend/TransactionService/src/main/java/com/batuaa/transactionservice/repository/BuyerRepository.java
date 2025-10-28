package com.batuaa.transactionservice.repository;

import com.batuaa.transactionservice.model.Buyer;
import com.batuaa.transactionservice.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BuyerRepository extends JpaRepository<Buyer, String> {

    boolean existsByEmailIdAndRole(String emailId, Role role);
    Optional<Buyer> findByEmailId(String emailId);

}
