package com.myBatuaa.service;

import com.myBatuaa.model.Role;
import com.myBatuaa.model.User;
import com.myBatuaa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
    public User registerUser(User user) {
        if (userRepository.existsByEmailId(user.getEmailId())) {
            throw new RuntimeException("User with this email already exists");
        }
        walletService.generateWalletId(user.getEmailId(), user.getMobileNumber());
        return userRepository.save(user);
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
