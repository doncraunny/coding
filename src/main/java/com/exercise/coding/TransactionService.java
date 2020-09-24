package com.exercise.coding;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    public ResponseEntity<List<Transaction>> getAllTransactions() {
        try {
            List<Transaction> transactions = new ArrayList<>();
            transactionRepository.findAll().forEach(transactions::add);
            return ResponseEntity.status(HttpStatus.OK).body(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Transaction> getTransactionById(Long id) {
        try {
            return transactionRepository.findById(id)
                    .map(transaction -> ResponseEntity.status(HttpStatus.OK).body(transaction))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Transaction> createTransaction(Transaction transaction) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(transactionRepository.save(transaction));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Transaction> updateTransaction(Long id, Transaction transaction) {
        try {
            Optional<Transaction> transactionOptional = transactionRepository.findById(id);
            if (transactionOptional.isPresent()) {
                transaction.setTransactionId(id);
                return ResponseEntity.status(HttpStatus.OK).body(transactionRepository.save(transaction));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<HttpStatus> deleteTransaction(Long id) {
        try {
            transactionRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
