package org.burgas.companyspringboot.dto.operation;

import lombok.*;
import org.burgas.companyspringboot.dto.Response;
import org.burgas.companyspringboot.entity.operation.OperationType;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class OperationShortResponse extends Response {

    private UUID id;
    private OperationType operationType;
    private Double amount;
    private String createdAt;
}
