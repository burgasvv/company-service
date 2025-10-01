package org.burgas.companyspringboot.dto.wallet;

import lombok.*;
import org.burgas.companyspringboot.dto.Response;
import org.burgas.companyspringboot.dto.identity.IdentityShortResponse;
import org.burgas.companyspringboot.dto.operation.OperationShortResponse;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class WalletFullResponse extends Response {

    private UUID id;
    private Double balance;
    private IdentityShortResponse identity;
    private List<OperationShortResponse> senderOperations;
    private List<OperationShortResponse> receiverOperations;
}
