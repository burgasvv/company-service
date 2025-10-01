package org.burgas.companyspringboot.dto.chat;

import lombok.*;
import org.burgas.companyspringboot.dto.Response;
import org.burgas.companyspringboot.dto.identity.IdentityShortResponse;
import org.burgas.companyspringboot.dto.message.MessageShortResponse;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class ChatFullResponse extends Response {

    private UUID id;
    private String name;
    private String description;
    private List<IdentityShortResponse> identities;
    private List<MessageShortResponse> messages;
    private String createdAt;
}
