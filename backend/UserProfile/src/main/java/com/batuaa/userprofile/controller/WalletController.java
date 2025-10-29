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
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("wallet/api/v1")
public class WalletController {
    @Autowired
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
    @GetMapping("/details/{walletId}")
    public ResponseEntity<ApiResponse> getWalletDetails(@PathVariable String walletId) {
        Wallet wallet = walletService.getWalletDetails(walletId);
        log.info("Wallet balance: " + wallet.getBalance());

        WalletResponseDto responseDto = new WalletResponseDto(wallet);
        return ResponseEntity.ok(new ApiResponse("success", "Wallet details fetched successfully", responseDto));
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

    @PutMapping("/set-primary")
    public ResponseEntity<ApiResponse> setPrimaryWallet(@RequestParam String walletId, @RequestParam String email) {
       walletService.setPrimaryWallet(walletId, email);
        return ResponseEntity.ok( new ApiResponse("success", "Primary wallet updated successfully"));
    }
    @GetMapping("/primary")
    public ResponseEntity<ApiResponse> getPrimaryWallet(@RequestParam String email) {
        Wallet wallet = walletService.getPrimaryWallet(email);
        return ResponseEntity.ok( new ApiResponse("success"," primary wallet fetched successfully",wallet));
    }

}


