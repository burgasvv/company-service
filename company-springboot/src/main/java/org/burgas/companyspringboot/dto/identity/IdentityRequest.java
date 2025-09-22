package org.burgas.companyspringboot.dto.identity;

import lombok.*;
import org.burgas.companyspringboot.dto.Request;
import org.burgas.companyspringboot.entity.identity.Authority;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class IdentityRequest extends Request {

    private UUID id;
    private Authority authority;
    private String username;
    private String password;
    private String phone;
    private Boolean enabled;
    private String firstname;
    private String lastname;
    private String patronymic;
    private String about;
    private UUID companyId;
}
