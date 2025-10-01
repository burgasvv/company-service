package org.burgas.companyspringboot.dto.identity;

import lombok.*;
import org.burgas.companyspringboot.dto.Response;
import org.burgas.companyspringboot.dto.chat.ChatShortResponse;
import org.burgas.companyspringboot.dto.company.CompanyShortResponse;
import org.burgas.companyspringboot.dto.wallet.WalletShortResponse;
import org.burgas.companyspringboot.entity.identity.Authority;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class IdentityFullResponse extends Response {

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
    private CompanyShortResponse company;
    private List<ChatShortResponse> chats;
    private List<WalletShortResponse> wallets;
}
