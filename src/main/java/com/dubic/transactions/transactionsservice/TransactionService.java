package com.dubic.transactions.transactionsservice;

import com.dubic.transactions.transactionsservice.models.Statistics;
import com.dubic.transactions.transactionsservice.models.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class TransactionService {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private Statistics statistics = new Statistics();
    private LocalDateTime referenceTimeStamp = null; // 30 seconds reference start time

    public boolean processTransaction(Transaction transaction) {
        log.info("Processing transaction : {}", transaction);
        if (!isWithin30Seconds(transaction)) {
            return false;
        }
        updateStatistics(transaction);
        return true;
    }

    /**
     * Checks if the transaction time is not 30 seconds past the reference start time
     *
     * @param transaction
     * @return true if transaction time is not 30 seconds past the reference start time
     */
    private boolean isWithin30Seconds(Transaction transaction) {
        // 30 seconds starts now with the first transaction
        if (this.referenceTimeStamp == null) {
            this.referenceTimeStamp = transaction.time();
            return true;
        }
//        LocalDateTime startTime = getOrStartReferenceTimestamp(transaction.time());
        System.out.println("Start time :: "+this.referenceTimeStamp);

        // Add 30 seconds to the reference start date and time
        LocalDateTime futureDateTime = this.referenceTimeStamp.plusSeconds(30);
        return transaction.time().isAfter(this.referenceTimeStamp) && transaction.time().isBefore(futureDateTime);
    }

//    private LocalDateTime getOrStartReferenceTimestamp(LocalDateTime transactionTime) {
//        if (this.referenceTimeStamp == null) {
//            this.referenceTimeStamp = transactionTime;
//        }
//        return this.referenceTimeStamp;
//    }

    private void updateStatistics(Transaction transaction) {
        this.statistics.update(transaction.amount());
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public void deleteTransactions() {
//        reset statistics and reference time
        this.statistics = new Statistics();
        this.referenceTimeStamp = null;
    }
}
