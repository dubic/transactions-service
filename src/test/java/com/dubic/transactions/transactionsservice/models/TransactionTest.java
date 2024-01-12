package com.dubic.transactions.transactionsservice.models;

import com.dubic.transactions.transactionsservice.data.TransactionRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    @DisplayName("Invalid amount string")
    public void createTransactionInvalidAmt() {
        assertThrows(NumberFormatException.class, () -> {
            TransactionRequest request = new TransactionRequest("12o,k", "2024-01-11T16:08:51.312Z");
            Transaction.createTransaction(request);
        });
    }

    @Test
    @DisplayName("Invalid timestamp string")
    public void createTransactionInvalidTime() {
        assertThrows(DateTimeParseException.class, () -> {
            TransactionRequest request = new TransactionRequest("120.281", "2024-01-116T16:08:51" +
                    ".312Z");
            Transaction.createTransaction(request);
        });
    }

    @Test
    @DisplayName("Future timestamp string")
    public void createTransactionFutureTime() {
        assertThrows(IllegalArgumentException.class, () -> {
            TransactionRequest request = new TransactionRequest("120.281",
                    "2025-01-11T16:08:51.312Z");
            Transaction.createTransaction(request);
        });
    }

    @Test
    @DisplayName("Valid amount and timestamp string")
    public void createTransactionValidAmtAndTime() {
        TransactionRequest request = new TransactionRequest("12.655", "2024-01-11T16:08:51.312Z");
        Transaction transaction = Transaction.createTransaction(request);
        assertEquals(new BigDecimal("12.655"), transaction.amount());
        assertTrue(transaction.time().isBefore(LocalDateTime.now(ZoneId.of("UTC"))));
    }
}