package com.batuaa.transactionservice.service;

import com.batuaa.transactionservice.model.Transaction;
import com.batuaa.transactionservice.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionLoggingService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logFailedTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }
}
