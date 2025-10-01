package org.burgas.companyspringboot.exception;

public class MatchesPasswordException extends RuntimeException {

    public MatchesPasswordException(String message) {
        super(message);
    }
}
