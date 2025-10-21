package com.batuaa.transactionservice.service;

import com.batuaa.transactionservice.dto.TransactionDateRangeDto;
import com.batuaa.transactionservice.dto.TransactionRemarkDto;
import com.batuaa.transactionservice.dto.TransactionTypeDto;
import com.batuaa.transactionservice.dto.TransferDto;
import com.batuaa.transactionservice.exception.EmptyTransactionListException;
import com.batuaa.transactionservice.exception.WalletNotFoundException;
import com.batuaa.transactionservice.model.Transaction;
import com.batuaa.transactionservice.repository.TransactionRepository;
import com.batuaa.transactionservice.repository.WalletRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TranscationSerivceImp implements TransactionService {

@Autowired
private  final TransactionRepository transactionRepository;
private final WalletRepository walletRepository;

@Autowired
    public TranscationSerivceImp(TransactionRepository transactionRepository, WalletRepository walletRepository) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
    }


    @Override
    public Transaction transferWalletToWallet(TransferDto transferDto) {
        return null;
    }

    // to filter transactions by date
    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findByWalletIdAndDateBetween(TransactionDateRangeDto dto) {
        try {
            // 1. Validate wallet existence
            boolean walletExists = walletRepository.existsByIdAndEmailId(dto.getWalletId(), dto.getEmailId());
            if (!walletExists) {
                throw new WalletNotFoundException("Wallet not found: " + dto.getWalletId());
            }

            // 2. Validate date range
            if (dto.getStartDate().isAfter(dto.getEndDate())) {
                throw new DateTimeException("Start date cannot be after end date");
            }

            LocalDate startDate = dto.getStartDate();
            LocalDate endDate = dto.getEndDate();
            LocalDate today = LocalDate.now();

            // 3. Prevent selecting future calendar dates
            if (startDate.isAfter(today) || endDate.isAfter(today)) {
                throw new DateTimeException("Start and end dates cannot be in the future");
            }

            // 4. Convert to time range (full day)
            LocalDateTime startOfDay = startDate.atStartOfDay();
            LocalDateTime endOfDay = endDate.atTime(LocalTime.MAX);

            // 5.Pagination setup (20 records per page)
            Pageable pageable = PageRequest.of(dto.getPage(), 20, Sort.by("timestamp").descending());

            // 6⃣ Fetch transactions from repo
            Page<Transaction> transactionsPage = transactionRepository.findByWalletAndTimestampRange(
                    dto.getWalletId(),
                    startOfDay,
                    endOfDay,
                    pageable
            );

            // [7]. Return results directly
            return transactionsPage.getContent();

        } catch (WalletNotFoundException | DateTimeException e) {
            log.error("Validation error while fetching transactions: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error fetching transactions for wallet {}: {}", dto.getWalletId(), e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    //to view transactions based on wallet id, email id, and type
    @Override
    @Transactional(readOnly = true)
    public List<Transaction> viewTransactionsByType(TransactionTypeDto transactionTypeDto) {
        try {
            // 1 Validate wallet existence
            boolean walletExists = walletRepository.existsByIdAndEmailId(
                    transactionTypeDto.getWalletId(),
                    transactionTypeDto.getEmailId()
            );

            if (!walletExists) {
                throw new WalletNotFoundException("Wallet not found: " + transactionTypeDto.getWalletId());
            }

            // 2.Fetch transactions by walletId and type
            List<Transaction> result = transactionRepository.findByWalletIdAndType(
                    transactionTypeDto.getWalletId(),transactionTypeDto.getWalletId(),
                    transactionTypeDto.getType()
            );

            if (result.isEmpty()) {
                throw new EmptyTransactionListException("No transactions found for wallet: " + transactionTypeDto.getWalletId());
            }

            // 3. Sort by timestamp (newest first)
            return result.stream()
                    .sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
                    .collect(Collectors.toList());

        } catch (WalletNotFoundException e) {
            log.error("Validation error while fetching transactions: {}", e.getMessage());
        } catch (EmptyTransactionListException e) {
            log.warn(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error fetching transactions for wallet {}: {}",
                    transactionTypeDto.getWalletId(), e.getMessage(), e);
        }

        return Collections.emptyList();
    }



    @Override
    public List<Transaction> filterTransactionsByRemark(TransactionRemarkDto transactionRemarkDto) {
        return List.of();
    }

    @Override
    public List<Transaction> getAllTransactions(String emailId, String walletId) throws EmptyTransactionListException{
        List<Transaction> result = transactionRepository.findByEmailAndWallet(emailId, walletId);

        if (result.isEmpty()) {
            throw new EmptyTransactionListException("No transactions found for wallet: " + walletId);
        }

        return result.stream()
                .sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
                .collect(Collectors.toList());

    }

    @Override
    public List<Transaction> sortTransactionsByAmount(String emailId, String walletId) {
        return List.of();
    }

    @Override
    public Transaction getTransactionByWalletId(String emailId, String walletId) {
        return null;
    }
}
