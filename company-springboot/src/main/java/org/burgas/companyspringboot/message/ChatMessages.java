package org.burgas.companyspringboot.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatMessages {

    MESSAGE_NOT_FOUND("Message not found"),
    CHAT_NOT_FOUND("Chat not found"),
    CONTENT_FIELD_EMPTY("Content field is empty"),
    SENDER_FIELD_EMPTY("Sender field is empty"),
    CHAT_FIELD_EMPTY("Chat field is empty");

    private final String message;
}
