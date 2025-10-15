package com.mybatuaa.service;

import com.mybatuaa.exception.UnauthorizedAccessException;
import com.mybatuaa.exception.UserAlreadyRegisteredException;
import com.mybatuaa.model.Role;
import com.mybatuaa.model.User;
import com.mybatuaa.model.Wallet;
import com.mybatuaa.repository.UserRepository;
import com.mybatuaa.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    public UserServiceImpl(UserRepository userRepository, WalletService walletService, WalletRepository walletrepository) {
        this.userRepository = userRepository;
        this.walletService = walletService;
        this.walletrepository = walletrepository;
    }

    private final UserRepository userRepository;
    private  final WalletService walletService;
    private final WalletRepository walletrepository;

    // to register for  new user
    @Override
    public User registerUser(User user) {
        String normalizedEmail = user.getEmailId().trim().toLowerCase();
        user.setEmailId(normalizedEmail);
        // Check if email already exists
        if (userRepository.existsByEmailIdIgnoreCase(normalizedEmail)) {

            throw new UserAlreadyRegisteredException("User already registered");
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

    @Override
    public User login(String emailId, String password, Role role) {
        String normalizedEmail = emailId.toLowerCase();

        return userRepository.findByEmailId(normalizedEmail)
                .filter(user -> user.getPassword().equals(password))
                .filter(user -> user.getRole() == role)

                .orElseThrow(() -> new UnauthorizedAccessException("Invalid email, password, or role"));
    }
}

