package org.burgas.companyspringboot.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IdentityMessages {

    IDENTITY_NOT_IN_CHAT("Identity not in chat"),
    MATCHES_PASSWORD("Matched identity passwords: new and old"),
    EMPTY_PASSWORD("Identity password is empty"),
    IDENTITY_NOT_AUTHORIZED("Identity not authorized"),
    IDENTITY_NOT_AUTHENTICATED("Identity not authenticated"),
    AUTHORITY_FIELD_EMPTY("Authority field is empty"),
    USERNAME_FIELD_EMPTY("Username field is empty"),
    PASSWORD_FIELD_EMPTY("Password field is empty"),
    PHONE_FIELD_EMPTY("Phone field is empty"),
    FIRSTNAME_FIELD_EMPTY("Firstname field is empty"),
    LASTNAME_FIELD_EMPTY("Lastname field is empty"),
    PATRONYMIC_FIELD_EMPTY("Patronymic field is empty"),
    ABOUT_FIELD_EMPTY("About field is empty"),
    COMPANY_FIELD_EMPTY("Company field is empty"),
    IDENTITY_NOT_FOUND("Identity not found");

    private final String message;
}
