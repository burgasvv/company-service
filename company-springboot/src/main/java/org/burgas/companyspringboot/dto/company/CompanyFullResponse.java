package org.burgas.companyspringboot.dto.company;

import lombok.*;
import org.burgas.companyspringboot.dto.Response;
import org.burgas.companyspringboot.dto.identity.IdentityShortResponse;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class CompanyFullResponse extends Response {

    private UUID id;
    private String name;
    private String description;
    private List<IdentityShortResponse> identities;
}
