package com.atm;

import java.math.BigDecimal;


public class ATMApplication {

    public static void main(String[] args) {
        CurrencyConverter converter = CurrencyConverter.withSampleRates();
        ATM atm = new ATM(converter);

        // Demo data - replace with a repository/database in a real system.
        Account demo = new Account("1001", "Alice Johnson", "1234");
        demo.deposit("USD", new BigDecimal("1000.00"), "opening balance");
        demo.deposit("EUR", new BigDecimal("500.00"), "opening balance");
        atm.registerAccount(demo);

        new ConsoleUI(atm).run();
    }
}
