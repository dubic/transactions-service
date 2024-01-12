package com.dubic.transactions.transactionsservice;

import com.dubic.transactions.transactionsservice.data.StatisticsResponse;
import com.dubic.transactions.transactionsservice.data.TransactionRequest;
import com.dubic.transactions.transactionsservice.models.Statistics;
import com.dubic.transactions.transactionsservice.models.Transaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

class TransactionServiceTest {

    @Test
    @DisplayName("30 seconds transactions")
    public void processTransactionWithin30Secs() {
        //Given first transaction
        TransactionService transactionService = new TransactionService();
        transactionService.processTransaction(
                Transaction.createTransaction(
                        new TransactionRequest("12.655", "2024-01-11T16:08:51.312Z")
                )
        );
        //when next transaction
        boolean isWithin30Secs = transactionService.processTransaction(
                Transaction.createTransaction(
                        new TransactionRequest("12.655", "2024-01-11T16:08:51.312Z")
                )
        );
        //Then
        assertTrue(isWithin30Secs);
    }

    @Test
    @DisplayName("past 30 seconds transactions")
    public void processTransactionPast30Secs() {
        TransactionService transactionService = new TransactionService();
//Given first transaction
        transactionService.processTransaction(
                Transaction.createTransaction(
                        new TransactionRequest("12.655", "2024-01-11T16:08:10.312Z")
                )
        );
        //when next transaction
        boolean isWithin30Secs = transactionService.processTransaction(
                Transaction.createTransaction(
                        new TransactionRequest("12.655", "2024-01-11T16:08:41.312Z")
                )
        );
        //Then
        assertFalse(isWithin30Secs);
    }

    @Test
    @DisplayName("Non null statistics")
    public void getInitialStatistics() {
        TransactionService transactionService = new TransactionService();
        Statistics statistics = transactionService.getStatistics();
        assertNotNull(statistics);
        assertEquals(BigDecimal.ZERO, statistics.getSum());
        assertEquals(BigDecimal.ZERO, statistics.getAvg());
        assertEquals(BigDecimal.ZERO, statistics.getMax());
        assertEquals(BigDecimal.ZERO, statistics.getMin());
        assertEquals(0L, statistics.getCount());
    }

    @Test
    @DisplayName("Transactions statistics")
    public void getTransactionsStatistics() {
        TransactionService transactionService = new TransactionService();
        //txn 1
        transactionService.processTransaction(
                Transaction.createTransaction(
                        new TransactionRequest("12.655", "2024-01-11T16:08:10.312Z")
                )
        );
        //txn 2
        transactionService.processTransaction(
                Transaction.createTransaction(
                        new TransactionRequest("4.239", "2024-01-11T16:08:10.312Z")
                )
        );
        //txn 3
        transactionService.processTransaction(
                Transaction.createTransaction(
                        new TransactionRequest("8.102", "2024-01-11T16:08:10.312Z")
                )
        );
        Statistics statistics = transactionService.getStatistics();
        StatisticsResponse r = StatisticsResponse.fromStatistics(statistics);
        assertEquals("25.00", r.sum());
        assertEquals("8.33", r.avg());
        assertEquals("12.655",r.max());
        assertEquals("4.239", r.min());
        assertEquals("3", r.count());
    }

    @Test
    @DisplayName("Reset statistics")
    public void deleteTransactions() {
//Given a transaction
        TransactionService transactionService = new TransactionService();
        transactionService.processTransaction(
                Transaction.createTransaction(
                        new TransactionRequest("12.655", "2024-01-11T16:08:51.312Z")
                )
        );
        //when delete
        transactionService.deleteTransactions();
        //then
        Statistics statistics = transactionService.getStatistics();
        assertNotNull(statistics);
        assertEquals(BigDecimal.ZERO, statistics.getSum());
        assertEquals(BigDecimal.ZERO, statistics.getAvg());
        assertEquals(BigDecimal.ZERO, statistics.getMax());
        assertEquals(BigDecimal.ZERO, statistics.getMin());
        assertEquals(0L, statistics.getCount());
        //start time
        assertNull(transactionService.getReferenceStartTime());
    }

}