package com.dubic.transactions.transactionsservice.data;

public class TransactionRequest {
    private String amount;
    private String timestamp;

    public TransactionRequest(String amount, String timestamp) {
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public String getAmount() {
        return amount;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
