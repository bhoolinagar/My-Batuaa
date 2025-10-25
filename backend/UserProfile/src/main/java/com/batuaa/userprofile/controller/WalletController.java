package com.batuaa.userprofile.controller;

import com.batuaa.userprofile.dto.ApiResponse;
import com.batuaa.userprofile.dto.WalletDto;
import com.batuaa.userprofile.dto.WalletResponseDto;
import com.batuaa.userprofile.model.Wallet;
import com.batuaa.userprofile.service.WalletService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("wallet/api/v1")
public class WalletController {
   // @Autowired
    private final WalletService walletService;

    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    // Link Bank Account
    @PostMapping("/link-bank-account")
    public ResponseEntity<ApiResponse> linkBankAccountToWallet(@Valid @RequestBody WalletDto wallet) {
        String walletId = walletService.linkBankAccountToWallet(wallet);
        return ResponseEntity.status(201)
                .body(new ApiResponse("success", "Wallet generated successfully", walletId));
    }

    // Add Money to Wallet
    @PostMapping("/add-money")
    public ResponseEntity<ApiResponse> addMoneyToWallet(
            @RequestParam String walletId,
            @RequestParam BigDecimal amount) {

        String message = walletService.updateMoneyFromBank(walletId, amount);
        return ResponseEntity.ok(new ApiResponse("success", message));
    }

    //Get Wallet Details
    @GetMapping("/details")
    public ResponseEntity<ApiResponse> getWalletDetails(
            @RequestParam String email,
            @RequestParam String walletId) {

        Wallet wallet = walletService.getWalletDetails(email, walletId);
        log.info("Wallet balance: " + wallet.getBalance());
        return ResponseEntity.ok(new ApiResponse("success", "Wallet details fetched successfully", wallet));
    }

    @GetMapping("/wallet-list/{email}")
    public ResponseEntity<ApiResponse> getWalletListByBuyer(@PathVariable String email) {
        // Fetch Wallet list from service
        List<Wallet> walletList = walletService.getWalletListByBuyer(email);

        // Map to DTOs
        List<WalletResponseDto> responseList = walletList.stream()
                .map(WalletResponseDto::new)
                .toList(); // Use Collectors.toList() if Java <16

        return ResponseEntity.ok(
                new ApiResponse("success", "Wallet list details fetched successfully", responseList)
        );
    }

}


