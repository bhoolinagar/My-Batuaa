package com.batuaa.userprofile.service;

import com.batuaa.userprofile.dto.BuyerDto;
import com.batuaa.userprofile.dto.LoginDto;
import com.batuaa.userprofile.exception.RegistrationException;
import com.batuaa.userprofile.model.Buyer;
import com.batuaa.userprofile.repository.BuyerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.batuaa.userprofile.exception.LoginException;

@Service
public class BuyerServiceImpl implements BuyerService {

    @Autowired
    private BuyerRepository buyerRepository;

    @Override

    public Buyer registerBuyer(BuyerDto buyerDto) {

        if (buyerDto.getRole() == null) {
            throw new RegistrationException("Role must be provided (ADMIN or BUYER)");
        }

        String normalizedEmail = buyerDto.getEmailId().trim().toLowerCase();

        if (buyerRepository.existsByEmailIdIgnoreCase(normalizedEmail)) {
            throw new RegistrationException("Email already registered");
        }

        Buyer buyer = new Buyer();
        buyer.setEmailId(normalizedEmail);
        buyer.setName(buyerDto.getName().trim());
        buyer.setPassword(buyerDto.getPassword().trim());
        buyer.setRole(buyerDto.getRole());

        return buyerRepository.save(buyer);
    }

    @Override
    public Buyer validateBuyer(LoginDto loginDto) {
        String normalizedEmail = loginDto.getEmailId().trim().toLowerCase();
        String password = loginDto.getPassword().trim();
        return buyerRepository.findByEmailIdAndPassword(normalizedEmail, password)
                .orElseThrow(() -> new LoginException("Invalid email or password"));
    }
    }
