package org.burgas.companyspringboot.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WalletMessages {

    NOT_ENOUGH_MONEY("Not enough money on wallet"),
    WRONG_AMOUNT("Wrong amount"),
    WALLET_NOT_FOUND("Wallet not found"),
    IDENTITY_ENTITY_FIELD_EMPTY("Identity entity field is empty");

    private final String message;
}
