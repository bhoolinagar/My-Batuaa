package com.myBatuaa.service;

import com.myBatuaa.model.Role;
import com.myBatuaa.model.User;
import com.myBatuaa.model.Wallet;
import com.myBatuaa.repository.UserRepository;
import com.myBatuaa.repository.WalletRepository;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private  WalletService walletService;
    
    @Autowired
    private WalletRepository walletrepository;
    
   // @Autowired
   // private Wallet wallet;
    
    // to register for  new user
    public User registerUser(User user) {
        if (userRepository.existsByEmailId(user.getEmailId())) {
            throw new RuntimeException("User with this email already exists");
        }
     // 1. Save user first
        User savedUser = userRepository.save(user);

        // 2. Generate wallet
        String walletId = walletService.generateWalletId(savedUser.getEmailId(), savedUser.getMobileNumber());
        // create wallet
        Wallet wallet = new Wallet();
        wallet.setWalletId(walletId);
        wallet.setBalance(BigDecimal.valueOf(0.0));
        wallet.setUser(savedUser);
        wallet.setCreatedAt(LocalDateTime.now());

        // 3. Save wallet
        walletrepository.save(wallet);

        return savedUser;
    }

    
    public User login(String emailId, String password , Role role) {
        {
            return userRepository.findByEmailId(emailId)
                    .filter(user -> user.getPassword().equals(password))
                    .filter(user -> user.getRole() == role)
                    .orElseThrow(() -> new RuntimeException("Invalid email, password, or role"));
        }
        }

    }
