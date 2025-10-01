package org.burgas.companyspringboot.exception;

public class EmptyPasswordException extends RuntimeException {

    public EmptyPasswordException(String message) {
        super(message);
    }
}
