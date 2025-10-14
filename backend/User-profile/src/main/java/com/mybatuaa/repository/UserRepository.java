package com.mybatuaa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mybatuaa.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // For login and registration checks
    Optional<User> findByEmailId(String emailId);

    boolean existsByEmailId(String emailId);


}
