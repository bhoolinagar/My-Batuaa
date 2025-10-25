package com.batuaa.userprofile.exception;

import com.batuaa.userprofile.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<ApiResponse> handleRegistrationException(RegistrationException ex) {
        ApiResponse response = new ApiResponse("fail", ex.getMessage());
        return ResponseEntity.status(400).body(response);
    }
    @ExceptionHandler(LoginException.class)
    public ResponseEntity<ApiResponse> handleLoginException(LoginException ex) {
        ApiResponse response = new ApiResponse("fail", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }


    // Handle validation errors (from @Valid in DTO)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldError().getDefaultMessage();
        ApiResponse response = new ApiResponse("fail", errorMessage);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<ApiResponse> handleWalletNotFound(WalletNotFoundException ex) {
        ApiResponse response = new ApiResponse("fail", ex.getMessage());
        return ResponseEntity.status(404).body(response);
    }

    @ExceptionHandler(WalletAlreadyFound.class)
    public ResponseEntity<ApiResponse> handleWalletAlreadyFound(WalletAlreadyFound ex) {
        ApiResponse response = new ApiResponse("fail", ex.getMessage());
        return ResponseEntity.status(409).body(response);
    }

    @ExceptionHandler(BuyerNotFoundException.class)
    public ResponseEntity<ApiResponse> handleBuyerNotFound(BuyerNotFoundException ex) {
        ApiResponse response = new ApiResponse("fail", ex.getMessage());
        return ResponseEntity.status(404).body(response);
    }

    // Generic exception handler for all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleAllOtherExceptions(Exception ex) {
        ApiResponse response = new ApiResponse("error", "Something went wrong: " + ex.getMessage());
        return ResponseEntity.status(500).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgumentException(Exception ex) {
        ApiResponse response = new ApiResponse("fail", ex.getMessage());
        return ResponseEntity.status(400).body(response);
    }
}