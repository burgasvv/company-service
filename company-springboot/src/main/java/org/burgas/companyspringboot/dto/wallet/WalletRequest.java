package org.burgas.companyspringboot.dto.wallet;

import lombok.*;
import org.burgas.companyspringboot.dto.Request;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class WalletRequest extends Request {

    private UUID id;
    private UUID identityId;
}
