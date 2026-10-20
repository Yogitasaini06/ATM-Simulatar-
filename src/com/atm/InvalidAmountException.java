package com.atm;


public class InvalidAmountException extends AtmException {
    public InvalidAmountException(String message) {
        super(message);
    }
}
