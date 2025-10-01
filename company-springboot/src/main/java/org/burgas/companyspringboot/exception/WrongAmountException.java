package org.burgas.companyspringboot.exception;

public class WrongAmountException extends RuntimeException {

    public WrongAmountException(String message) {
        super(message);
    }
}
