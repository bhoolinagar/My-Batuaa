package com.batuaa.userprofile.controller;

import com.batuaa.userprofile.config.JwtUtil;
import com.batuaa.userprofile.dto.ApiResponse;
import com.batuaa.userprofile.dto.BuyerDto;
import com.batuaa.userprofile.model.Buyer;
import com.batuaa.userprofile.model.Role;
import com.batuaa.userprofile.service.BuyerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
//@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("buyers/api/v1")
public class BuyerController {

    @Autowired
    private BuyerService buyerService;

    @PostMapping("/register")
    public ResponseEntity<?> registerBuyer(@Valid @RequestBody BuyerDto buyerDto) {
        try {
            Buyer savedBuyer = buyerService.registerBuyer(buyerDto);

            BuyerDto responseDto = new BuyerDto();
            responseDto.setName(savedBuyer.getName());
            responseDto.setEmailId(savedBuyer.getEmailId());
            responseDto.setRole(savedBuyer.getRole());
            responseDto.setPassword(savedBuyer.getPassword());

            String message = savedBuyer.getRole() == Role.ADMIN ? "Admin registered successfully"
                    : "Buyer registered successfully";

            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("success", message, responseDto));

        } catch (RuntimeException e) {
            // Handle duplicate email or invalid role
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("fail", e.getMessage(), null));
        } catch (Exception e) {
            // Catch-all fallback (avoid 500 in tests)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("fail", "Something went wrong", null));
        }
    }

    // Login Buyer (Returns JWT Token)
    @PostMapping("/login")
    public ResponseEntity<?> loginBuyer(@RequestBody BuyerDto buyerDto) {
        // Validate user credentials
        Buyer buyer = buyerService.validateBuyer(buyerDto);

        if (buyer != null) {
            // Generate JWT token

            String token = JwtUtil.generateToken(buyer.getEmailId(), buyer.getRole().name());

            // Role-based message
            String message = buyer.getRole() == Role.ADMIN ? "Admin login successful" : "Buyer login successful";

            // Return token + role if needed
            Map<String, String> data = Map.of("token", token, "role", buyer.getRole().name(),"email",
                    buyer.getEmailId(),"name", buyer.getName());

            // Return consistent ApiResponse
            return ResponseEntity.ok(new ApiResponse("success", message, data));
        } else {
            // Invalid credentials
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse("fail", "Invalid credentials", null));
        }
    }
}