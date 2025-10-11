package com.myBatuaa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myBatuaa.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // For login and registration checks
    Optional<User> findByEmailId(String emailId);

    boolean existsByEmailId(String emailId);


}
