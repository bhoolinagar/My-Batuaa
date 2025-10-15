package com.mybatuaa.service;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mybatuaa.exception.AmountCanNotBeNullException;
import com.mybatuaa.exception.BankNotFoundException;
import com.mybatuaa.exception.EmptyTransactionListException;
import com.mybatuaa.exception.ErrorOccurredUnableToFilterByRemarkException;
import com.mybatuaa.exception.InsufficientFundsException;
import com.mybatuaa.exception.TransactionNotFoundException;
import com.mybatuaa.exception.UnableToAddMoneyException;
import com.mybatuaa.exception.WalletNotFoundException;
import com.mybatuaa.model.BankAccount;
import com.mybatuaa.model.Status;
import com.mybatuaa.model.Transaction;
import com.mybatuaa.model.Wallet;
import com.mybatuaa.repository.BankAccountResposity;
import com.mybatuaa.repository.TransactionRepository;
import com.mybatuaa.repository.WalletRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TransactionServiceImp implements TransactionService {
    private  final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private  final BankAccountResposity bankAccountResposity;

	@Autowired
	public TransactionServiceImp(TransactionRepository transactionRepository, WalletRepository walletRepository,
			BankAccountResposity bankAccountResposity) {
		this.transactionRepository = transactionRepository;
		this.walletRepository = walletRepository;
		this.bankAccountResposity = bankAccountResposity;
	}

	/*
	 * Functional Purpose: Transaction Getting Recorded, Once Money Is Added To The
	 * Wallet Transaction object creation.Transactional will ensure atomicity: if
	 * any exception will occur, both wallets and transaction rollback.
	 */
	@Transactional
	@Override
	public Transaction addMoney(String walletIdTo, String accountNumber, BigDecimal amount) {
		if (amount == null) {
			throw new AmountCanNotBeNullException("Amount Should Not Be Null");
		}
		try {
			BankAccount source = bankAccountResposity.findById(accountNumber)
					.orElseThrow(() -> new BankNotFoundException("Bank account not found: " + accountNumber));
			Wallet destination = source.getWallet();
			if (destination == null) {
				throw new WalletNotFoundException("Wallet not linked to bank account: " + accountNumber);
			}
			if (source.getBalance().compareTo(amount) < 0) {
				throw new InsufficientFundsException("Insufficient funds");
			}
			source.setBalance(source.getBalance().subtract(amount));
			destination.setBalance(destination.getBalance().add(amount));
			bankAccountResposity.save(source);
			walletRepository.save(destination);
			Transaction transaction = new Transaction();
			transaction.setAccount(source);
			transaction.setToWallet(destination);
			transaction.setAmount(amount);
			transaction.setTimestamp(LocalDateTime.now());
			transaction.setStatus(Status.SUCCESS);
			transaction.setRemarks("Money added to wallet " + walletIdTo);
			Transaction saved = transactionRepository.save(transaction);
			log.info("Transaction successful: {} transferred from bank account {} to wallet {}", amount, accountNumber,
					walletIdTo);
			return saved;
		} catch (BankNotFoundException | WalletNotFoundException | InsufficientFundsException e) {
			log.error("Transaction failed for account: {} | Reason: {}", accountNumber, e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error while adding money | Wallet: {}, Account: {}, Error: {}", walletIdTo,
					accountNumber, e.getMessage(), e);
			throw new UnableToAddMoneyException("Error occurred", e);
		}
	}

	/*
	 * Functional Purpose: Transactions created when money Transferred between
	 * wallets
	 */
	@Transactional
	@Override
	public Transaction transferWalletToWallet(String walletIdFrom, String walletIdTo, BigDecimal amount) {
		if (amount == null) {
			throw new AmountCanNotBeNullException("Amount Should Not Be Null");
		}
		try {
			Wallet walletFrom = walletRepository.findByWalletId(walletIdFrom)
					.orElseThrow(() -> new WalletNotFoundException("Sender wallet not found: " + walletIdFrom));

			Wallet walletTo = walletRepository.findByWalletId(walletIdTo)
					.orElseThrow(() -> new WalletNotFoundException("Receiver wallet not found" + walletIdTo));
			if (walletFrom.getBalance().compareTo(amount) < 0) {
				throw new InsufficientFundsException("Insufficient funds");
			}

			walletFrom.setBalance(walletFrom.getBalance().subtract(amount));
			walletTo.setBalance(walletTo.getBalance().add(amount));

			walletRepository.save(walletFrom);
			walletRepository.save(walletTo);
//           Sender
			Transaction senderTransaction = new Transaction();

			senderTransaction.setFromWallet(walletFrom);
			senderTransaction.setToWallet(walletTo);
			senderTransaction.setAmount(amount);
			senderTransaction.setTimestamp(LocalDateTime.now());
			senderTransaction.setStatus(Status.SUCCESS);
			senderTransaction.setRemarks("Transferred Rs" + amount + " to wallet " + walletIdTo);

			transactionRepository.save(senderTransaction);
//            Receiver
			Transaction receiverTransaction = new Transaction();

			receiverTransaction.setFromWallet(walletFrom);
			receiverTransaction.setToWallet(walletTo);
			receiverTransaction.setAmount(amount);
			receiverTransaction.setTimestamp(LocalDateTime.now());
			receiverTransaction.setStatus(Status.SUCCESS);
			receiverTransaction.setRemarks("Received Rs" + amount + " from wallet " + walletIdFrom);

			transactionRepository.save(receiverTransaction);
			log.info("Wallet transfer successful: {} transferred from wallet {} to wallet {}", amount, walletIdFrom,
					walletIdTo);
			return senderTransaction;
		} catch (WalletNotFoundException | InsufficientFundsException e) {
			log.error("Wallet transfer failed | From: {}, To: {}, Amount: {} | Reason: {}", walletIdFrom, walletIdTo,
					amount, e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error during wallet transfer | From: {}, To: {}, Amount: {}, Error: {}", walletIdFrom,
					walletIdTo, amount, e.getMessage(), e);
			throw new UnableToAddMoneyException("Error occurred", e);
		}
	}

	/*
	 * Functional Purpose: Filter Transactions On Basis Of Remark
	 */
	@Override
	public List<Transaction> filterTransactionsByRemark(String walletId, String remark) {
		try {
			Wallet wallet = Optional.ofNullable(walletId).filter(id -> !id.isBlank())
					.flatMap(walletRepository::findByWalletId)
					.orElseThrow(() -> new WalletNotFoundException("Wallet Not Found or is invalid"));

			String key = Optional.ofNullable(remark).orElse("");
			List<Transaction> transactions = transactionRepository
					.findByWalletAndRemarkContainingIgnoreCase(wallet.getWalletId(), key);

			if (transactions.isEmpty() && !key.isBlank()) {
				log.warn("No transaction is there for wallet {} with remark '{}' ", walletId, key);
				throw new TransactionNotFoundException(
						"No transaction found for wallet " + walletId + "with remark: " + key);
			}

			log.info("Filtered {} transactions for wallet {} with remark containing '{}'", transactions.size(),
					walletId, key);
			return transactions;

		} catch (WalletNotFoundException e) {
			log.error("Wallet Not Found or Invalid: {}", walletId, e);
			throw e;
		} catch (Exception e) {
			log.error("Error occurred while filteringtransactions for wallet {} with remark'{}': {} ", walletId, remark,
					e.getMessage(), e);
			throw new ErrorOccurredUnableToFilterByRemarkException("Error occurred while filtering by remarks");
		}

	}

	// view Translations history by custom date range
	@Override
	public List<Transaction> filterTransactionsByDateRange(String walletId, LocalDate startDate, LocalDate endDate) {
		try {
			if (!walletRepository.existsById(walletId)) {
				throw new WalletNotFoundException("Wallet not found: " + walletId);
			}
			// Validate date range
			if (startDate.isAfter(endDate)) {
				throw new DateTimeException("Start date cannot be after end date");
			}
			LocalDateTime startOfDay = startDate.atStartOfDay();
			LocalDateTime endOfDay = endDate.atTime(LocalTime.MAX);
			LocalDateTime currentDate = LocalDateTime.now();

			// Ensure both dates are not in the future
			if (startOfDay.isAfter(currentDate) || endOfDay.isAfter(currentDate)) {
				throw new DateTimeException("Start and end date should be before or equal to the current date");
			}
			// Fetch transactions within the date range
			List<Transaction> transactions = transactionRepository
					.findByFromWallet_WalletIdOrToWallet_WalletIdAndTimestampBetween(walletId, walletId, startOfDay,
							endOfDay);
            List<Transaction> transactionList = transactions.stream().sorted(Comparator.comparing(Transaction::getTimestamp))
                    .collect(Collectors.toList());
            return transactionList;
		} catch (WalletNotFoundException | DateTimeException e) {
			log.error("Wallet not found or start and end date should be before or equal to the current date");
			throw e; // Let upper layers handle known exceptions
		} catch (Exception e) {
			log.error("Error fetching transactions by date for walletId {}: {}", walletId, e.getMessage(), e);
			return Collections.emptyList();
		}
	}

	// View transaction by Type (based on type-success, failed, processing)
	@Override
	public List<Transaction> filterTransactionByType(String walletId, Status type) {
		try {
			List<Transaction> transactions = transactionRepository.findByFromWallet_WalletIdAndStatus(walletId, type);
			if (transactions == null || transactions.isEmpty()) {
				log.error("No transactions found for wallet ID: " + walletId);
				throw new EmptyTransactionListException("No transactions found for wallet ID: " + walletId);
			}
			return transactions.stream().sorted(Comparator.comparing(Transaction::getTimestamp))
					.collect(Collectors.toList());
		} catch (EmptyTransactionListException e) {
			throw new EmptyTransactionListException("No transactions found for wallet ID: " + walletId);
		}
	}


    @Override
    public List<Transaction> getAllTransactions(String walletId) {
        return transactionRepository.findByFromWallet_WalletIdOrToWallet_WalletId(walletId);

    }
}
