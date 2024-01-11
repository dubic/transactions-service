package com.dubic.transactions.transactionsservice.data;

import com.dubic.transactions.transactionsservice.models.Statistics;

import java.math.BigDecimal;

import static com.dubic.transactions.transactionsservice.models.Transaction.MATH_CONTEXT;

public record StatisticsResponse(String sum, String avg, String max, String min, String count) {
    public static StatisticsResponse fromStatistics(Statistics statistics) {
        String sum = statistics.getSum()
                .setScale(MATH_CONTEXT.getPrecision(), MATH_CONTEXT.getRoundingMode()).toString();
        String avg = statistics.getAvg()
                .setScale(MATH_CONTEXT.getPrecision(), MATH_CONTEXT.getRoundingMode()).toString();
        String max = statistics.getMax()
                .setScale(MATH_CONTEXT.getPrecision(), MATH_CONTEXT.getRoundingMode()).toString();
        String min = statistics.getMin()
                .setScale(MATH_CONTEXT.getPrecision(), MATH_CONTEXT.getRoundingMode()).toString();
        String count = String.valueOf(statistics.getCount());

        return new StatisticsResponse(sum, avg, max, min, count);
    }
}
