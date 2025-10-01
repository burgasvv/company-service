package org.burgas.companyspringboot.exception;

public class OperationNotFoundException extends RuntimeException {

    public OperationNotFoundException(String message) {
        super(message);
    }
}
