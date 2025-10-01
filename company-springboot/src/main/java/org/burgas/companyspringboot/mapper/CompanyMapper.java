package org.burgas.companyspringboot.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.companyspringboot.dto.company.CompanyFullResponse;
import org.burgas.companyspringboot.dto.company.CompanyRequest;
import org.burgas.companyspringboot.dto.company.CompanyShortResponse;
import org.burgas.companyspringboot.entity.company.Company;
import org.burgas.companyspringboot.repository.CompanyRepository;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.burgas.companyspringboot.message.CompanyMessages.DESCRIPTION_FIELD_EMPTY;
import static org.burgas.companyspringboot.message.CompanyMessages.NAME_FIELD_EMPTY;

@Component
@RequiredArgsConstructor
public final class CompanyMapper implements EntityMapper<CompanyRequest, Company, CompanyShortResponse, CompanyFullResponse> {

    private final CompanyRepository companyRepository;
    private final ObjectFactory<IdentityMapper> identityMapperObjectFactory;

    private IdentityMapper getIdentityMapper() {
        return this.identityMapperObjectFactory.getObject();
    }

    @Override
    public Company toEntity(CompanyRequest companyRequest) {
        UUID companyId = this.handleData(
                companyRequest.getId(), UUID.nameUUIDFromBytes(new byte[]{})
        );
        return this.companyRepository.findById(companyId)
                .map(
                        company -> {
                            String name = this.handleData(companyRequest.getName(), company.getName());
                            String description = this.handleData(companyRequest.getDescription(), company.getDescription());
                            return Company.builder()
                                    .id(company.getId())
                                    .name(name)
                                    .description(description)
                                    .build();
                        }
                )
                .orElseGet(
                        () -> {
                            String name = this.handleDataThrowable(companyRequest.getName(), NAME_FIELD_EMPTY.getMessage());
                            String description = this.handleDataThrowable(companyRequest.getDescription(),
                                    DESCRIPTION_FIELD_EMPTY.getMessage());
                            return Company.builder()
                                    .name(name)
                                    .description(description)
                                    .build();
                        }
                );
    }

    @Override
    public CompanyShortResponse toShortResponse(Company company) {
        return CompanyShortResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .description(company.getDescription())
                .build();
    }

    @Override
    public CompanyFullResponse toFullResponse(Company company) {
        return CompanyFullResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .description(company.getDescription())
                .identities(
                        company.getIdentities()
                                .stream()
                                .map(identity -> this.getIdentityMapper().toShortResponse(identity))
                                .toList()
                )
                .build();
    }
}
