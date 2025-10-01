package org.burgas.companyspringboot.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OperationMessages {

    OPERATION_NOT_FOUND("Operation not found"),
    OPERATION_TYPE_FIELD_EMPTY("Operation type field is empty"),
    AMOUNT_FIELD_EMPTY("Amount field is empty"),
    SENDER_WALLET_FIELD_EMPTY("Sender wallet field is empty"),
    RECEIVER_WALLET_FIELD_EMPTY("Receiver wallet field is empty");

    private final String message;
}
