package org.burgas.companyspringboot.dto.chat;

import lombok.*;
import org.burgas.companyspringboot.dto.Response;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class ChatShortResponse extends Response {

    private UUID id;
    private String name;
    private String description;
    private String createdAt;
}
