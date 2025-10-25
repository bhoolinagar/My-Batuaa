package com.batuaa.userprofile.repository;

import com.batuaa.userprofile.model.Buyer;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuyerRepository extends JpaRepository<Buyer, Integer> {

    // Check if email is already registered (ignore case)
    boolean existsByEmailIdIgnoreCase(String emailId);
    Optional<Buyer> findByEmailId(String emailId);
    Optional<Buyer> findByEmailIdAndPassword(String emailId,String password);
}
