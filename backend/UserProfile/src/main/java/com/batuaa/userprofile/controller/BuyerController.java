package com.batuaa.userprofile.controller;

import com.batuaa.userprofile.config.JwtUtil;
import com.batuaa.userprofile.dto.ApiResponse;
import com.batuaa.userprofile.dto.BuyerDto;
import com.batuaa.userprofile.dto.LoginDto;
import com.batuaa.userprofile.model.Buyer;
import com.batuaa.userprofile.model.Role;
import com.batuaa.userprofile.service.BuyerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
//@RequiredArgsConstructor
@RequestMapping("buyers/api/v1")
public class BuyerController {

    @Autowired
    private BuyerService buyerService;

    @PostMapping("/register")
    public ResponseEntity<?> registerBuyer(@Valid @RequestBody BuyerDto buyerDto) {

            Buyer responseDto = buyerService.registerBuyer(buyerDto);
            String message = buyerDto.getRole() == Role.ADMIN ? "Admin registered successfully"
                    : "Buyer registered successfully";
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("success", message, responseDto));

    }

    // Login Buyer (Returns JWT Token)
    @PostMapping("/login")
    public ResponseEntity<?> loginBuyer(@RequestBody LoginDto loginDto) {
        Buyer buyer = buyerService.validateBuyer(loginDto); // throws LoginException if invalid

        String token = JwtUtil.generateToken(buyer.getEmailId(), buyer.getRole().name());
        String message = buyer.getRole() == Role.ADMIN ? "Admin login successful" : "Buyer login successful";

        Map<String, String> data = Map.of("token", token, "role", buyer.getRole().name());
        return ResponseEntity.ok(new ApiResponse("success", message, data));
    }
}