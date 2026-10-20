package com.atm;

import java.math.BigDecimal;
import java.time.Instant;


public record Transaction(Instant timestamp, String type, String currency,
                          BigDecimal amount, String details) {
    @Override
    public String toString() {
        return String.format("%s | %-12s | %s %s | %s",
                timestamp.toString().substring(0, 19).replace('T', ' '),
                type, amount.toPlainString(), currency, details);
    }
}
