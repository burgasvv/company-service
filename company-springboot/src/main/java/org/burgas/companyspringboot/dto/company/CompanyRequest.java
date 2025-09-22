package org.burgas.companyspringboot.dto.company;

import lombok.*;
import org.burgas.companyspringboot.dto.Request;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class CompanyRequest extends Request {

    private UUID id;
    private String name;
    private String description;
}
