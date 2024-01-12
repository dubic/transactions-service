package com.dubic.transactions.transactionsservice.models;

import com.dubic.transactions.transactionsservice.data.TransactionRequest;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public record Transaction(BigDecimal amount, LocalDateTime time) {
    public static final MathContext MATH_CONTEXT = new MathContext(2, RoundingMode.HALF_UP);

    public static Transaction createTransaction(TransactionRequest request)
            throws NumberFormatException, DateTimeParseException {
        if (request.getAmount() == null) {
            throw new NullPointerException("Amount must be supplied");
        }
        if (request.getTimestamp() == null) {
            throw new NullPointerException("Timestamp must be supplied");
        }
        BigDecimal txnAmount = new BigDecimal(request.getAmount());
        LocalDateTime txnDate = parseTransactionDate(request.getTimestamp());
        return new Transaction(txnAmount, txnDate);
    }

    /**
     * Parses the transaction timestamp string to a valid date. Since ISO 8601 format is used
     * with LocalDateTime, no format is required.
     *
     * @param timestamp transaction time in the ISO 8601 format YYYY-MM-DDThh:mm:ss.sssZ
     * @return the parsed date time
     * @throws DateTimeParseException
     */
    private static LocalDateTime parseTransactionDate(String timestamp) throws DateTimeParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        LocalDateTime txnDate = LocalDateTime.parse(timestamp, formatter);
        //Reject future dates
        if (txnDate.isAfter(LocalDateTime.now(ZoneId.of("UTC")))) {
            throw new IllegalArgumentException("Transaction date cannot be past current date time");
        }
        return txnDate;
    }
}
