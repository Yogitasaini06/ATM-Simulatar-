package com.atm;

import java.math.BigDecimal;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ConsoleUI {

    private final ATM atm;
    private final Scanner in = new Scanner(System.in);

    public ConsoleUI(ATM atm) {
        this.atm = atm;
    }

    public void run() {
        System.out.println("=== Welcome to the Java ATM ===");
        try {
            while (true) {
                if (!atm.isLoggedIn()) {
                    if (!loginScreen()) break;
                } else {
                    mainMenu();
                }
            }
        } catch (NoSuchElementException eof) {
            System.out.println(); 
        }
        System.out.println("Goodbye!");
    }

    /** @return false if the user chose to quit the application. */
    private boolean loginScreen() {
        System.out.print("\nAccount number (or 'q' to quit): ");
        String acc = in.nextLine().trim();
        if (acc.equalsIgnoreCase("q")) return false;
        System.out.print("PIN: "); // Use System.console().readPassword() in a real terminal
        String pin = in.nextLine().trim();
        try {
            Account a = atm.login(acc, pin);
            System.out.println("Welcome, " + a.getHolderName() + "!");
        } catch (AtmException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return true;
    }

    private void mainMenu() {
        System.out.println("""

                --- MAIN MENU ---
                1. Check balances
                2. Deposit
                3. Withdraw
                4. Exchange between wallets
                5. Mini statement
                6. Supported currencies
                0. Log out""");
        System.out.print("Choose: ");
        String choice = in.nextLine().trim();
        try {
            switch (choice) {
                case "1" -> showBalances();
                case "2" -> deposit();
                case "3" -> withdraw();
                case "4" -> exchange();
                case "5" -> atm.miniStatement(10).forEach(System.out::println);
                case "6" -> System.out.println("Supported: " + atm.getConverter().supportedCurrencies());
                case "0" -> { atm.logout(); System.out.println("Logged out."); }
                default -> System.out.println("Invalid option.");
            }
        } catch (AtmException e) {
            System.out.println("Error: " + e.getMessage()); // all domain errors handled gracefully
        }
    }

    private void showBalances() {
        Map<String, BigDecimal> b = atm.checkBalances();
        if (b.isEmpty()) System.out.println("No funds on account.");
        b.forEach((cur, amt) -> System.out.printf("  %s: %s%n", cur, amt.toPlainString()));
    }

    private void deposit() {
        String cash = prompt("Currency of cash inserted [" + CurrencyConverter.BASE_CURRENCY + "]: ");
        BigDecimal amt = readAmount("Amount: ");
        String wallet = prompt("Wallet to credit (blank = same currency): ");
        if (wallet.isEmpty()) wallet = cash;
        BigDecimal credited = atm.deposit(amt, cash, wallet);
        System.out.println("Deposit successful. Credited: " + credited.toPlainString() + " " + wallet.toUpperCase());
    }

    private void withdraw() {
        String cash = prompt("Currency to dispense [" + CurrencyConverter.BASE_CURRENCY + "]: ");
        BigDecimal amt = readAmount("Amount: ");
        String wallet = prompt("Wallet to debit (blank = same currency): ");
        if (wallet.isEmpty()) wallet = cash;
        BigDecimal debited = atm.withdraw(amt, cash, wallet);
        System.out.println("Please take your cash. Debited: " + debited.toPlainString() + " " + wallet.toUpperCase());
    }

    private void exchange() {
        String from = prompt("From wallet: ");
        String to = prompt("To wallet: ");
        BigDecimal amt = readAmount("Amount of " + from.toUpperCase() + " to convert: ");
        BigDecimal got = atm.exchange(amt, from, to);
        System.out.println("Exchanged. Received: " + got.toPlainString() + " " + to.toUpperCase());
    }

   

    /** Blank input falls back to the default currency placeholder. */
    private String prompt(String label) {
        System.out.print(label);
        String s = in.nextLine().trim();
        return (s.isEmpty() && label.contains("[" + CurrencyConverter.BASE_CURRENCY + "]"))
                ? CurrencyConverter.BASE_CURRENCY : s;
    }

    private BigDecimal readAmount(String label) {
        System.out.print(label);
        String raw = in.nextLine().trim();
        try {
            return new BigDecimal(raw);
        } catch (NumberFormatException e) {
            throw new InvalidAmountException("'" + raw + "' is not a valid number.");
        }
    }
}
