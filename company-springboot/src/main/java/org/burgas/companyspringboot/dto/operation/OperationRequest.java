package org.burgas.companyspringboot.dto.operation;

import lombok.*;
import org.burgas.companyspringboot.dto.Request;
import org.burgas.companyspringboot.entity.operation.OperationType;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class OperationRequest extends Request {

    private UUID id;
    private OperationType operationType;
    private Double amount;
    private UUID senderWalletId;
    private UUID receiverWalletId;
}
