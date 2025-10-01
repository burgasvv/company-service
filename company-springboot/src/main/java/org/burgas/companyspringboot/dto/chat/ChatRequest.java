package org.burgas.companyspringboot.dto.chat;

import lombok.*;
import org.burgas.companyspringboot.dto.Request;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class ChatRequest extends Request {

    private UUID id;
    private String name;
    private String description;
}
