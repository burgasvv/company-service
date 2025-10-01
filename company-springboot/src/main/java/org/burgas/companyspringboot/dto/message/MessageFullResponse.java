package org.burgas.companyspringboot.dto.message;

import lombok.*;
import org.burgas.companyspringboot.dto.Response;
import org.burgas.companyspringboot.dto.chat.ChatShortResponse;
import org.burgas.companyspringboot.dto.identity.IdentityShortResponse;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class MessageFullResponse extends Response {

    private UUID id;
    private String content;
    private IdentityShortResponse sender;
    private ChatShortResponse chat;
    private String createdAt;
}
