package com.batuaa.userprofile.service;

import com.batuaa.userprofile.dto.BuyerDto;
import com.batuaa.userprofile.model.Buyer;
import com.batuaa.userprofile.repository.BuyerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BuyerServiceImpl implements BuyerService {

    @Autowired
    private BuyerRepository buyerRepository;

    @Override
    public Buyer registerBuyer(BuyerDto buyerDto) {

        if (buyerDto.getRole() == null) {
            throw new RuntimeException("Role must be provided (ADMIN or BUYER)");
        }
        String normalizedEmail = buyerDto.getEmailId().trim().toLowerCase();

        if (buyerRepository.existsByEmailIdIgnoreCase(normalizedEmail)) {
            throw new RuntimeException("Email already registered");
        }

        // Map DTO to Entity
        Buyer buyer = new Buyer();
        buyer.setEmailId(normalizedEmail);
        buyer.setName(buyerDto.getName().trim());
        buyer.setPassword(buyerDto.getPassword().trim());

        // Set role from DTO if provided, else default to BUYER
        buyer.setRole(buyerDto.getRole());

        return buyerRepository.save(buyer);
    }

    @Override
    public Buyer validateBuyer(BuyerDto buyerDto) {
        String normalizedEmail = buyerDto.getEmailId().trim().toLowerCase();

        Optional<Buyer> optBuyer = buyerRepository.findByEmailId(normalizedEmail);

        if (optBuyer.isPresent()) {
            Buyer buyer = optBuyer.get();
            // if (passwordEncoder.matches(buyerDto.getPassword(), buyer.getPassword())) {
            if (buyer.getPassword().equals(buyerDto.getPassword())) {
                return buyer;
            }
        }
        return null;
    }
}