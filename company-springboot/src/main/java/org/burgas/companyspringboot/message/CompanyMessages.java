package org.burgas.companyspringboot.message;

import lombok.*;

@Getter
@RequiredArgsConstructor
public enum CompanyMessages {

    COMPANY_NOT_FOUND("Company not found"),
    NAME_FIELD_EMPTY("Name field is empty"),
    DESCRIPTION_FIELD_EMPTY("Description field is empty");

    private final String message;
}
