package com.batuaa.transactionservice.service;

import com.batuaa.transactionservice.dto.TransactionDateRangeDto;
import com.batuaa.transactionservice.dto.TransactionRemarkDto;
import com.batuaa.transactionservice.dto.TransactionTypeDto;
import com.batuaa.transactionservice.dto.TransferDto;
import com.batuaa.transactionservice.exception.*;
import com.batuaa.transactionservice.model.*;
import com.batuaa.transactionservice.repository.BuyerRepository;
import com.batuaa.transactionservice.repository.TransactionRepository;
import com.batuaa.transactionservice.repository.WalletRepository;
import jakarta.transaction.InvalidTransactionException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TranscationSerivceImp implements TransactionService {


private  final TransactionRepository transactionRepository;
private final WalletRepository walletRepository;

private BuyerRepository buyerRepository;

@Autowired
private TransactionLoggingService transactionLoggingService;

@Autowired
public TranscationSerivceImp(TransactionRepository transactionRepository, WalletRepository walletRepository, BuyerRepository buyerRepository) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
    this.buyerRepository = buyerRepository;
    }
// wallet to wallet transfer

    @Override
    @Transactional
    public Transaction transferWalletToWallet(@Valid TransferDto transferDto) {
        Transaction senderTransaction = null;
        try {
            Wallet walletFrom = walletRepository.findByWalletId(transferDto.getFromWalletId())
                    .orElseThrow(() -> new WalletNotFoundException(
                            "Sender wallet not found: " + transferDto.getFromWalletId()));
            Wallet walletTo = walletRepository.findByWalletId(transferDto.getToWalletId())
                    .orElseThrow(() -> new WalletNotFoundException(
                            "Receiver wallet not found: " + transferDto.getToWalletId()));

            // Sender and receiver wallets should be different

            if (walletFrom.getWalletId().equals(walletTo.getWalletId())) {
                throw new InvalidWalletTransactionException("Sender and Receiver wallets cannot be the same.");
            }

            // Validating that provided receiver email matches the wallet’s ToBuyer email
            if (!walletTo.getBuyer().getEmailId().equalsIgnoreCase(transferDto.getToBuyerEmailId())) {
                throw new InvalidEmailAddressException("Receiver email does not match the provided wallet ID.");
            }


            // Check for duplicate transaction
            Optional<Transaction> existingTransaction = transactionRepository
                    .findByFromWalletAndToWalletAndAmountAndStatus(
                            walletFrom, walletTo, transferDto.getAmount(), Status.PROCESSING);

            if (existingTransaction.isPresent()) {
                throw new DuplicateTransactionException(
                        "A similar transaction is already in process from " +
                                transferDto.getFromWalletId() + " to " + transferDto.getToWalletId());
            }

            // Check for insufficient funds before proceeding
            if (walletFrom.getBalance().compareTo(transferDto.getAmount()) < 0) {
                // Logging the  transaction as failed for sender-only transaction
                senderTransaction = new Transaction(
                        walletFrom, walletTo, walletFrom.getBuyer(), walletTo.getBuyer(),
                        transferDto.getAmount(), LocalDateTime.now(),
                        Status.FAILED, (transferDto.getRemarks() != null && !transferDto.getRemarks().trim().isEmpty())
                        ? transferDto.getRemarks()
                        : "Transfer failed: Insufficient funds in your wallet.",
                        Type.WITHDRAWN);

                log.warn("Transfer failed due to insufficient funds | Wallet: {}, Amount: {}",
                        transferDto.getFromWalletId(), transferDto.getAmount());

                throw new InsufficientFundsException("Insufficient funds in your selected wallet");
            }

            // Sender transaction
            senderTransaction = new Transaction(walletFrom, walletTo, walletFrom.getBuyer(), walletTo.getBuyer(),
                    transferDto.getAmount(), LocalDateTime.now(), Status.PROCESSING,
                    "Initiating transfer of Rs " + transferDto.getAmount() + " to wallet " + transferDto.getToWalletId(),
                    Type.WITHDRAWN);
            transactionRepository.save(senderTransaction);
            log.info("Sender transaction created | ID: {}, Amount: {}", senderTransaction.getTransactionId(), transferDto.getAmount());

            //update balances
            walletFrom.setBalance(walletFrom.getBalance().subtract(transferDto.getAmount()));
            walletTo.setBalance(walletTo.getBalance().add(transferDto.getAmount()));
            walletRepository.save(walletFrom);
            walletRepository.save(walletTo);

            // Transaction done successfully
            senderTransaction.setStatus(Status.SUCCESS);
            senderTransaction.setRemarks(transferDto.getRemarks());
            transactionRepository.save(senderTransaction);
            // Receiver transaction
            Transaction receiverTransaction = new Transaction(walletFrom, walletTo, walletFrom.getBuyer(), walletTo.getBuyer(),
                    transferDto.getAmount(), LocalDateTime.now(), Status.SUCCESS,
                    transferDto.getRemarks(),
                    Type.RECEIVED);
            transactionRepository.save(receiverTransaction);
            log.info("Receiver transaction created | ID: {},Amount: {}", receiverTransaction.getTransactionId(), transferDto.getAmount());

            log.info("Wallet transfer successful | From: {}, To: {},Amount: {}", transferDto.getFromWalletId(), transferDto.getToWalletId(), transferDto.getAmount());

            return senderTransaction;

        } catch (WalletNotFoundException | InsufficientFundsException | DuplicateTransactionException | InvalidWalletTransactionException e) {
            log.error("Wallet transfer failed | From: {}, To: {},Amount: {} | Reason: {}",
                    transferDto.getFromWalletId(), transferDto.getToWalletId(), transferDto.getAmount(), e.getMessage(), e);

            if (senderTransaction != null) {
                senderTransaction.setStatus(Status.FAILED);
                senderTransaction.setRemarks("Transfer failed: " + e.getMessage());
                transactionLoggingService.logFailedTransaction(senderTransaction);

            }
            throw e;

        } catch (InvalidEmailAddressException e) {
            log.error("Unexpected error during wallet transfer | From: {}, To: {}, Amount: {}, Error: {}",
                    transferDto.getFromWalletId(), transferDto.getToWalletId(), transferDto.getAmount(), e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    // to filter transactions by date
    @Override
    public List<Transaction> findByWalletIdAndDateBetween(TransactionDateRangeDto dto) {

        // 1. Validate wallet existence
        if (!walletRepository.existsByIdAndEmailId(dto.getWalletId(), dto.getEmailId())) {
            throw new WalletNotFoundException("Wallet not found: " + dto.getWalletId());
        }

        // 2. Validate date range order
        if (dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new DateTimeException("Start date cannot be after end date.");
        }

        // 3. Prepare LocalDateTime boundaries
        LocalDateTime startOfDay = dto.getStartDate().atStartOfDay();
        LocalDateTime now = LocalDateTime.now();
        // If end date is today, end time is now; otherwise end of that day
        LocalDateTime endOfDay = dto.getEndDate().isEqual(LocalDate.now())
                ? now
                : dto.getEndDate().atTime(LocalTime.MAX);

        // 4. Disallow any future date (start or end)
        if (startOfDay.isAfter(now) || endOfDay.isAfter(now)) {
            throw new DateTimeException("Start and end dates must not be in the future.");
        }

        // 5. Setup pagination (20 records per page)
        Pageable pageable = PageRequest.of(dto.getPage(), 20, Sort.by("timestamp").descending());

        // 6. Fetch transactions within range (for both sent/received)
        Page<Transaction> transactionsPage = transactionRepository.findByWalletAndTimestampRange(
                dto.getWalletId(),
                startOfDay,
                endOfDay,
                pageable
        );

        // 7. Handle empty results
        if (transactionsPage.isEmpty()) {
            throw new EmptyTransactionListException("No transactions found for the given date range.");
        }

        // 8. Return sorted list (descending by timestamp)
        return transactionsPage.getContent()
                .stream().filter((time) -> time.getTimestamp() != null)
                .sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    //to view transactions based on wallet id, email id, and type
    @Override
    @Transactional(readOnly = true)
    public List<Transaction> viewTransactionsByType(TransactionTypeDto transactionTypeDto) {

        // 1. Validate wallet existence
        boolean walletExists = walletRepository.existsByIdAndEmailId(
                transactionTypeDto.getWalletId(),
                transactionTypeDto.getEmailId()
        );

        if (!walletExists) {
            throw new WalletNotFoundException("Wallet Id or email not found: " + transactionTypeDto.getWalletId());
        }

        // 2. Fetch transactions based on type
        List<Transaction> result;
        if (Type.WITHDRAWN.equals(transactionTypeDto.getType())) {
            result = transactionRepository.findByFromWalletIdAndType(
                    transactionTypeDto.getWalletId(),
                    transactionTypeDto.getType()
            );
        } else if (Type.RECEIVED.equals(transactionTypeDto.getType())) {
            result = transactionRepository.findByToWalletIdAndType(
                    transactionTypeDto.getWalletId(),
                    transactionTypeDto.getType()
            );
        } else {
            throw new IllegalArgumentException("Invalid transaction type: " + transactionTypeDto.getType());
        }

        // 3. Handle empty list
        if (result.isEmpty()) {
            throw new EmptyTransactionListException(
                    "No transactions found for wallet: " + transactionTypeDto.getWalletId() + " and type: " + transactionTypeDto.getType()
            );
        }
        // 4. Sort by newest first
        return result.stream()
                .sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
                .collect(Collectors.toList());

    }


    //    Filter Transactions By Remarks
    @Transactional
    @Override
    public List<Transaction> filterTransactionsByRemark(TransactionRemarkDto transactionRemarkDto) {
        try {
            List<Transaction> transactions = transactionRepository.findByWalletAndEmailAndRemarkNative(
                    transactionRemarkDto.getWalletId(), transactionRemarkDto.getEmailId(), transactionRemarkDto.getRemark());

            // handling empty results
            if (transactions.isEmpty()) {
                log.warn("No transactions found for wallet {} with remark '{}'", transactionRemarkDto.getWalletId(), transactionRemarkDto.getRemark());
                throw new TransactionNotFoundException("No transaction found for wallet " + transactionRemarkDto.getWalletId() + " with remark: " + transactionRemarkDto.getRemark());
            }
            log.info("Filtered {} transactions for wallet {} with remark '{}'", transactions.size(), transactionRemarkDto.getWalletId(), transactionRemarkDto.getRemark());
            return transactions;

        } catch (TransactionNotFoundException e) {
            log.warn(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error filtering transactions for wallet {} with remark '{}'", transactionRemarkDto.getWalletId(), transactionRemarkDto.getRemark(), e);
            throw new UnableToFilterByRemarkException("Error occurred while filtering transactions by remark" + e);
        }
    }

    @Override
    public List<Transaction> getAllTransactions(String emailId, String walletId) {
        List<Transaction> result = transactionRepository.findByEmailAndWallet(emailId, emailId, walletId, walletId);

        if (result.isEmpty()) {
            throw new EmptyTransactionListException("No transactions found for wallet: " + walletId);
        }

        return result.stream()
                .sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
                .collect(Collectors.toList());

    }

    @Override
    public List<Transaction> sortTransactionsByAmount(String walletId, String emailId, String sortOrder) {
        List<Transaction> transactionList = transactionRepository.findTransactionAmountByWalletAndEmail(walletId, emailId);
        if ("DESC".equalsIgnoreCase(sortOrder)) {
            return transactionList.stream().sorted(Comparator.comparing(Transaction::getAmount).reversed()).collect(Collectors.toList());
        } else {
            return transactionList.stream().sorted(Comparator.comparing(Transaction::getAmount)).collect(Collectors.toList());
        }
    }

    @Override
    public Transaction getTransactionByWalletId(String emailId, String walletId) {
        return null;
    }

    @Override
    public List<Transaction> findByAdminEmailAndRole(String email, Role role) {

        // Check if admin exists
        boolean isAdminExists = buyerRepository.existsByEmailIdAndRole(email, role);
        if (!isAdminExists) {
            throw new AdminNotFoundException("Admin not found with email: " + email);
        }

        // Fetch all transactions
        List<Transaction> transactions = transactionRepository.findAll();

        if (transactions.isEmpty()) {
            throw new EmptyTransactionListException("No transactions found.");
        }

        return transactions;
    }
}
