package com.dubic.transactions.transactionsservice.models;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static com.dubic.transactions.transactionsservice.models.Transaction.MATH_CONTEXT;

public class Statistics {

    private BigDecimal sum = new BigDecimal(0);
    private BigDecimal avg = new BigDecimal(0);
    private BigDecimal max = new BigDecimal(0);
    private BigDecimal min = new BigDecimal(0);
    private long count = 0L;

    public BigDecimal getSum() {
        return sum;
    }

    public BigDecimal getAvg() {
        return avg;
    }

    public BigDecimal getMax() {
        return max;
    }

    public BigDecimal getMin() {
        return min;
    }

    public long getCount() {
        return count;
    }

    public void updateMinimum(BigDecimal amount) {
        if (amount.compareTo(min) < 0 || min.compareTo(BigDecimal.ZERO) == 0) {
            this.min = amount;
        }
    }

    public void updateMaximum(BigDecimal amount) {
        if (amount.compareTo(max) > 0) {
            this.max = amount;
        }
    }

    public void updateSumAndAverage(BigDecimal amount) {
        this.count++;
        sum = amount.add(sum);

        BigDecimal countDecimal = new BigDecimal(""+count);
        avg = sum.divide(countDecimal, 10, RoundingMode.HALF_UP);
    }

    public synchronized void update(BigDecimal amount) {
        updateMinimum(amount);
        updateMaximum(amount);
        updateSumAndAverage(amount);
    }
}
