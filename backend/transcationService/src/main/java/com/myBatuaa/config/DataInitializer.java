// For testing purpose:
//package com.myBatuaa.config;
//
//import com.myBatuaa.model.*;
//import com.myBatuaa.repository.BankAccountResposity;
//import com.myBatuaa.repository.UserRepository;
//import com.myBatuaa.repository.WalletRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//@Configuration
//public class DataInitializer {
//
//    @Bean
//    CommandLineRunner initData(UserRepository userRepository,
//                               WalletRepository walletRepository,
//                               BankAccountResposity bankAccountResposity) {
//        return args -> {
//
//            // Users
//            User Khyati = new User();
//            Khyati.setName("Khyati");
//            Khyati.setEmailId("Kky@example.com");
//            Khyati.setGender(Gender.FEMALE);
//            Khyati.setMobileNumber("9999999999");
//            Khyati.setAddress("123 Main Street");
//            Khyati.setPassword("password123");
//            Khyati.setRole(Role.Buyer);
//
//            User Bhavesh = new User();
//            Bhavesh.setName("Bhavesh");
//            Bhavesh.setEmailId("Bobh@example.com");
//            Bhavesh.setGender(Gender.MALE);
//            Bhavesh.setMobileNumber("8888888888");
//            Bhavesh.setAddress("456 Park Avenue");
//            Bhavesh.setPassword("password456");
//            Bhavesh.setRole(Role.Buyer);
//
//            try {
//                userRepository.saveAll(List.of(Khyati, Bhavesh));
//            } catch (Exception e) {
//                System.out.println("Users already exist, skipping insert.");
//            }
//
//            // Wallets
//            Wallet wallet1 = new Wallet("WALLET001", Khyati, LocalDateTime.now(),
//                    BigDecimal.valueOf(5000), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
//
//            Wallet wallet2 = new Wallet("WALLET002", Bhavesh, LocalDateTime.now(),
//                    BigDecimal.valueOf(3000), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
//
//            try {
//                walletRepository.saveAll(List.of(wallet1, wallet2));
//            } catch (Exception e) {
//                System.out.println("Wallets already exist, skipping insert.");
//            }
//
//            // Bank accounts
//            BankAccount acc1 = new BankAccount("ACC001", 101, "HDFC", "HDFC0001",
//                    BigDecimal.valueOf(10000), wallet1);
//
//            BankAccount acc2 = new BankAccount("ACC002", 102, "ICICI", "ICIC0001",
//                    BigDecimal.valueOf(5000), wallet2);
//
//            try {
//                bankAccountResposity.saveAll(List.of(acc1, acc2));
//            } catch (Exception e) {
//                System.out.println("Bank accounts already exist, skipping insert.");
//            }
//
//            System.out.println("Static test data initialization complete.");
//        };
//    }
//}


//import com.myBatuaa.model.Transaction;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//
//@PostMapping("/add-money-from-bank")
//public ResponseEntity<Transaction> addMoneyFromBank(@RequestBody AddMoneyRequest request) {
//    Transaction transaction = transactionService.addMoney(
//            request.walletIdTo(),
//            request.accountNumber(),
//            request.amount()
//    );
//    return new ResponseEntity<>(transaction, HttpStatus.CREATED);
//}
//    @PostMapping("/transfer-wallet-to-wallet")
//    public ResponseEntity<Transaction> transferWalletToWallet(@RequestBody TransferRequest req) {
//        // Call the service layer
//        Transaction transaction = transactionService.transferWalletToWallet(
//                req.walletIdFrom(),  // source wallet
//                req.walletIdTo(),    // destination wallet
//                req.amount()         // transfer amount
//        );