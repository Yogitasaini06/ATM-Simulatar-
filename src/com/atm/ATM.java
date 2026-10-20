package com.atm;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ATM {

    
    private static final BigDecimal MAX_WITHDRAWAL_BASE = new BigDecimal("1000.00");
    
    private static final int MAX_PIN_ATTEMPTS = 3;

    private final Map<String, Account> accounts = new HashMap<>();
    private final Map<String, Integer> failedAttempts = new HashMap<>();
    private final CurrencyConverter converter;
    private Account session; 

    public ATM(CurrencyConverter converter) {
        this.converter = converter;
    }

    public void registerAccount(Account account) {
        accounts.put(account.getAccountNumber(), account);
    }



    public Account login(String accountNumber, String pin) {
        Account acc = accounts.get(accountNumber);
        if (acc == null) throw new AuthenticationException("Invalid account number or PIN.");

        int fails = failedAttempts.getOrDefault(accountNumber, 0);
        if (fails >= MAX_PIN_ATTEMPTS) {
            throw new AuthenticationException("Account is locked. Contact your bank.");
        }
        if (!acc.verifyPin(pin)) {
            failedAttempts.put(accountNumber, fails + 1);
            throw new AuthenticationException("Invalid account number or PIN. Attempts left: "
                    + (MAX_PIN_ATTEMPTS - fails - 1));
        }
        failedAttempts.remove(accountNumber);
        session = acc;
        return acc;
    }

    public void logout() { session = null; }
    public boolean isLoggedIn() { return session != null; }

   

    public Map<String, BigDecimal> checkBalances() {
        return requireSession().getBalances();
    }

    
    public BigDecimal deposit(BigDecimal amount, String cashCurrency, String walletCurrency) {
        Account acc = requireSession();
        String cash = converter.normalize(cashCurrency);
        String wallet = converter.normalize(walletCurrency);

        BigDecimal credit = converter.convert(amount, cash, wallet);
        String note = cash.equals(wallet) ? "cash" : "cash " + amount.toPlainString() + " " + cash;
        acc.deposit(wallet, credit, note);
        return credit;
    }

    
    public BigDecimal withdraw(BigDecimal amount, String cashCurrency, String walletCurrency) {
        Account acc = requireSession();
        String cash = converter.normalize(cashCurrency);
        String wallet = converter.normalize(walletCurrency);

        if (amount == null || amount.signum() <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero.");
        }
        BigDecimal inBase = converter.convert(amount, cash, CurrencyConverter.BASE_CURRENCY);
        if (inBase.compareTo(MAX_WITHDRAWAL_BASE) > 0) {
            throw new InvalidAmountException("Exceeds per-transaction limit of "
                    + MAX_WITHDRAWAL_BASE.toPlainString() + " " + CurrencyConverter.BASE_CURRENCY);
        }
        BigDecimal debit = converter.convert(amount, cash, wallet);
        String note = cash.equals(wallet) ? "cash" : "cash " + amount.toPlainString() + " " + cash;
        acc.withdraw(wallet, debit, note); // overdraft check happens here
        return debit;
    }

    
    public BigDecimal exchange(BigDecimal amount, String from, String to) {
        Account acc = requireSession();
        String f = converter.normalize(from);
        String t = converter.normalize(to);
        if (f.equals(t)) throw new InvalidAmountException("Source and target currencies are the same.");

        BigDecimal received = converter.convert(amount, f, t);
        acc.exchange(f, amount, t, received);
        return received;
    }

    public List<Transaction> miniStatement(int count) {
        return requireSession().getRecentTransactions(count);
    }

    public CurrencyConverter getConverter() { return converter; }

    private Account requireSession() {
        if (session == null) throw new AuthenticationException("Please log in first.");
        return session;
    }
}
