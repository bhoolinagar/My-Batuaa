package com.mybatuaa.repository;

import com.mybatuaa.model.User;
import jakarta.validation.constraints.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // For login and registration checks
    Optional<User> findByEmailId(String emailId);

    // For login and registration checks
    boolean existsByEmailIdIgnoreCase(String normalizedEmail);

    Object existsByEmailId(@Email String emailId);


}
