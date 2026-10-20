package com.atm;


public class UnknownCurrencyException extends AtmException {
    public UnknownCurrencyException(String code) {
        super("Unsupported currency code: '" + code + "'");
    }
}
