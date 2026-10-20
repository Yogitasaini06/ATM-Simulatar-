# Java ATM Simulation System

Console ATM with multi-currency wallets, deposits, withdrawals and currency exchange
(hardcoded sample rates). Requires **JDK 17 or newer**.

## Run
- Linux / macOS: `./build-and-run.sh`
- Windows:       `build-and-run.bat`

Or manually:
    mkdir out
    javac -d out src/com/atm/*.java
    java -cp out com.atm.ATMApplication

## Demo login
Account `1001`, PIN `1234` (starts with 1000 USD and 500 EUR).
Supported currencies: USD (base), EUR, GBP, INR, JPY.

## Structure
- ATMApplication  - entry point, seeds demo data
- ConsoleUI       - Scanner menu loop (presentation only)
- ATM             - service layer: login, PIN lockout, withdrawal limit, currency routing
- Account         - domain: per-currency wallets, hashed PIN, transaction history
- CurrencyConverter - mock exchange rates (edit withSampleRates() to change them)
- Transaction     - immutable history record
- *Exception      - typed domain errors

## Business-rule placeholders (in code)
- [DEFAULT_CURRENCY]       CurrencyConverter.BASE_CURRENCY
- [INITIAL_EXCHANGE_RATES] CurrencyConverter.withSampleRates()
- [MAX_WITHDRAWAL_LIMIT]   ATM.MAX_WITHDRAWAL_BASE (1000 USD equivalent)
- [MAX_PIN_ATTEMPTS]       ATM.MAX_PIN_ATTEMPTS (3)
