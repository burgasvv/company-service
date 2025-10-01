package org.burgas.companyspringboot.exception;

public class IdentityNotInChatException extends RuntimeException {

    public IdentityNotInChatException(String message) {
        super(message);
    }
}
