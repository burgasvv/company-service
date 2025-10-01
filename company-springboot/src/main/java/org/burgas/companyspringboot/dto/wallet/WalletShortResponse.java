package org.burgas.companyspringboot.dto.wallet;

import lombok.*;
import org.burgas.companyspringboot.dto.Response;
import org.burgas.companyspringboot.dto.identity.IdentityShortResponse;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class WalletShortResponse extends Response {

    private UUID id;
    private Double balance;
    private IdentityShortResponse identity;
}
