package com.batuaa.userprofile.repository;

import com.batuaa.userprofile.model.Buyer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BuyerRepository extends JpaRepository<Buyer, Integer> {

    // Check if email is already registered (ignore case)
    boolean existsByEmailIdIgnoreCase(String emailId);

    // Find buyer by email (for login validation)
    Optional<Buyer> findByEmailId(String emailId);
}
