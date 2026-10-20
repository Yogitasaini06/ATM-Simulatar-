package com.atm;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class Account {

    private final String accountNumber;
    private final String holderName;
    private final String pinHash;
    private final Map<String, BigDecimal> wallets = new TreeMap<>();
    private final List<Transaction> history = new ArrayList<>();

    public Account(String accountNumber, String holderName, String pin) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.pinHash = hash(pin);
    }

    public String getAccountNumber() { return accountNumber; }
    public String getHolderName()    { return holderName; }

    public boolean verifyPin(String pin) {
        return MessageDigest.isEqual(pinHash.getBytes(StandardCharsets.UTF_8),
                                     hash(pin).getBytes(StandardCharsets.UTF_8));
    }

    public synchronized BigDecimal getBalance(String currency) {
        return wallets.getOrDefault(currency, BigDecimal.ZERO.setScale(2));
    }

    public synchronized Map<String, BigDecimal> getBalances() {
        return Collections.unmodifiableMap(new TreeMap<>(wallets));
    }

    public synchronized void deposit(String currency, BigDecimal amount, String details) {
        requireValidAmount(amount);
        wallets.merge(currency, scaled(amount), BigDecimal::add);
        history.add(new Transaction(Instant.now(), "DEPOSIT", currency, scaled(amount), details));
    }

    public synchronized void withdraw(String currency, BigDecimal amount, String details) {
        requireValidAmount(amount);
        debit(currency, amount);
        history.add(new Transaction(Instant.now(), "WITHDRAWAL", currency, scaled(amount), details));
    }

    /** Atomically moves value between two wallets (amounts already converted by the caller). */
    public synchronized void exchange(String fromCur, BigDecimal fromAmt,
                                      String toCur, BigDecimal toAmt) {
        requireValidAmount(fromAmt);
        requireValidAmount(toAmt);
        debit(fromCur, fromAmt); // throws before any change if funds are insufficient
        wallets.merge(toCur, scaled(toAmt), BigDecimal::add);
        Instant now = Instant.now();
        history.add(new Transaction(now, "EXCHANGE_OUT", fromCur, scaled(fromAmt), "to " + toCur));
        history.add(new Transaction(now, "EXCHANGE_IN", toCur, scaled(toAmt), "from " + fromCur));
    }

    public synchronized List<Transaction> getRecentTransactions(int limit) {
        int from = Math.max(0, history.size() - limit);
        return new ArrayList<>(history.subList(from, history.size()));
    }


    private void debit(String currency, BigDecimal amount) {
        BigDecimal balance = getBalance(currency);
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException(String.format(
                    "Insufficient %s funds: requested %s, available %s",
                    currency, scaled(amount).toPlainString(), balance.toPlainString()));
        }
        wallets.put(currency, balance.subtract(scaled(amount)));
    }

    private static void requireValidAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero.");
        }
        if (amount.stripTrailingZeros().scale() > 2) {
            throw new InvalidAmountException("Amount cannot have more than 2 decimal places.");
        }
    }

    private static BigDecimal scaled(BigDecimal v) {
        return v.setScale(2, RoundingMode.UNNECESSARY);
    }

    
    private static String hash(String pin) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(pin.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
