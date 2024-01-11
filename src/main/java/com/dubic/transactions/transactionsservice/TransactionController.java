package com.dubic.transactions.transactionsservice;

import com.dubic.transactions.transactionsservice.data.StatisticsResponse;
import com.dubic.transactions.transactionsservice.models.Statistics;
import com.dubic.transactions.transactionsservice.data.TransactionRequest;
import com.dubic.transactions.transactionsservice.models.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/transaction")
public class TransactionController {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<?> postTransaction(@RequestBody TransactionRequest transactionRequest) {
        try {
            Transaction transaction = Transaction.createTransaction(transactionRequest);
            boolean success = this.transactionService.processTransaction(transaction);
            if (success) {
                return ResponseEntity.status(HttpStatus.CREATED).build();
            }
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (DateTimeParseException | IllegalArgumentException e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
    }

    @GetMapping
    public ResponseEntity<StatisticsResponse> transactionStatistics() {
        Statistics statistics = transactionService.getStatistics();
        StatisticsResponse statisticsResponse = StatisticsResponse.fromStatistics(statistics);
        return ResponseEntity.ok(statisticsResponse);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAllTransactions() {
        this.transactionService.deleteTransactions();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
