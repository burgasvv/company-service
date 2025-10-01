package org.burgas.companyspringboot.dto.message;

import lombok.*;
import org.burgas.companyspringboot.dto.Request;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class MessageRequest extends Request {

    private UUID id;
    private String content;
    private UUID senderId;
    private UUID chatId;
}
