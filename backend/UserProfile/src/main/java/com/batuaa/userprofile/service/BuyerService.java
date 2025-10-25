package com.batuaa.userprofile.service;

import com.batuaa.userprofile.dto.BuyerDto;
import com.batuaa.userprofile.dto.LoginDto;
import com.batuaa.userprofile.model.Buyer;

public interface BuyerService {

    // Register a new Buyer
    Buyer registerBuyer(BuyerDto buyerDto);

    // Validate login credentials, returns Buyer if valid, null if invalid
    Buyer validateBuyer(LoginDto loginDto);
}
