package org.burgas.companyspringboot.dto.message;

import lombok.*;
import org.burgas.companyspringboot.dto.Response;
import org.burgas.companyspringboot.dto.identity.IdentityShortResponse;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class MessageShortResponse extends Response {

    private UUID id;
    private String content;
    private IdentityShortResponse sender;
    private String createdAt;
}
