package org.burgas.companyspringboot.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.companyspringboot.dto.identity.IdentityFullResponse;
import org.burgas.companyspringboot.dto.identity.IdentityRequest;
import org.burgas.companyspringboot.dto.identity.IdentityShortResponse;
import org.burgas.companyspringboot.entity.company.Company;
import org.burgas.companyspringboot.entity.identity.Authority;
import org.burgas.companyspringboot.entity.identity.Identity;
import org.burgas.companyspringboot.repository.CompanyRepository;
import org.burgas.companyspringboot.repository.IdentityRepository;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static org.burgas.companyspringboot.message.IdentityMessages.*;

@Component
@RequiredArgsConstructor
public final class IdentityMapper implements EntityMapper<IdentityRequest, Identity, IdentityShortResponse, IdentityFullResponse> {

    private final IdentityRepository identityRepository;
    private final ObjectFactory<CompanyRepository> companyRepositoryObjectFactory;
    private final ObjectFactory<CompanyMapper> companyMapperObjectFactory;
    private final PasswordEncoder passwordEncoder;

    private CompanyRepository getCompanyRepository() {
        return this.companyRepositoryObjectFactory.getObject();
    }

    private CompanyMapper getCompanyMapper() {
        return this.companyMapperObjectFactory.getObject();
    }

    @Override
    public Identity toEntity(IdentityRequest identityRequest) {
        UUID identityId = this.handleData(
                identityRequest.getId(), UUID.nameUUIDFromBytes(new byte[]{})
        );
        return this.identityRepository.findById(identityId)
                .map(
                        identity -> {
                            Authority authority = this.handleData(identityRequest.getAuthority(), identity.getAuthority());
                            String username = this.handleData(identityRequest.getUsername(), identity.getUsername());
                            String phone = this.handleData(identityRequest.getPhone(), identity.getPhone());
                            Boolean enabled = this.handleData(identityRequest.getEnabled(), identity.getEnabled());
                            String firstname = this.handleData(identityRequest.getFirstname(), identity.getFirstname());
                            String lastname = this.handleData(identityRequest.getLastname(), identity.getLastname());
                            String patronymic = this.handleData(identityRequest.getPatronymic(), identity.getPatronymic());
                            String about = this.handleData(identityRequest.getAbout(), identity.getAbout());
                            UUID companyId = this.handleData(identityRequest.getCompanyId(), UUID.nameUUIDFromBytes(new byte[]{}));
                            Company company = this.handleData(
                                    this.getCompanyRepository().findById(companyId).orElse(null),
                                    identity.getCompany()
                            );
                            return Identity.builder()
                                    .id(identity.getId())
                                    .authority(authority)
                                    .username(username)
                                    .password(identity.getPassword())
                                    .phone(phone)
                                    .enabled(enabled)
                                    .firstname(firstname)
                                    .lastname(lastname)
                                    .patronymic(patronymic)
                                    .about(about)
                                    .company(company)
                                    .build();
                        }
                )
                .orElseGet(
                        () -> {
                            Authority authority = this.handleDataThrowable(identityRequest.getAuthority(),
                                    AUTHORITY_FIELD_EMPTY.getMessage());
                            String username = this.handleDataThrowable(identityRequest.getUsername(), USERNAME_FIELD_EMPTY.getMessage());
                            String password = this.handleDataThrowable(identityRequest.getPassword(), PASSWORD_FIELD_EMPTY.getMessage());
                            String phone = this.handleDataThrowable(identityRequest.getPhone(), PHONE_FIELD_EMPTY.getMessage());
                            String firstname = this.handleDataThrowable(identityRequest.getFirstname(), FIRSTNAME_FIELD_EMPTY.getMessage());
                            String lastname = this.handleDataThrowable(identityRequest.getLastname(), LASTNAME_FIELD_EMPTY.getMessage());
                            String patronymic = this.handleDataThrowable(identityRequest.getPatronymic(), PATRONYMIC_FIELD_EMPTY.getMessage());
                            String about = this.handleDataThrowable(identityRequest.getAbout(), ABOUT_FIELD_EMPTY.getMessage());
                            UUID companyId = this.handleData(identityRequest.getCompanyId(), UUID.nameUUIDFromBytes(new byte[]{}));
                            Company company = this.getCompanyRepository().findById(companyId).orElse(null);
                            return Identity.builder()
                                    .authority(authority)
                                    .username(username)
                                    .password(this.passwordEncoder.encode(password))
                                    .phone(phone)
                                    .enabled(true)
                                    .firstname(firstname)
                                    .lastname(lastname)
                                    .patronymic(patronymic)
                                    .about(about)
                                    .company(company)
                                    .build();
                        }
                );
    }

    @Override
    public IdentityShortResponse toShortResponse(Identity identity) {
        return IdentityShortResponse.builder()
                .id(identity.getId())
                .authority(identity.getAuthority())
                .username(identity.getUsername())
                .password(identity.getPassword())
                .phone(identity.getPhone())
                .enabled(identity.getEnabled())
                .firstname(identity.getFirstname())
                .lastname(identity.getLastname())
                .patronymic(identity.getPatronymic())
                .about(identity.getAbout())
                .build();
    }

    @Override
    public IdentityFullResponse toFullResponse(Identity identity) {
        return IdentityFullResponse.builder()
                .id(identity.getId())
                .authority(identity.getAuthority())
                .username(identity.getUsername())
                .password(identity.getPassword())
                .phone(identity.getPhone())
                .enabled(identity.getEnabled())
                .firstname(identity.getFirstname())
                .lastname(identity.getLastname())
                .patronymic(identity.getPatronymic())
                .about(identity.getAbout())
                .company(
                        Optional.ofNullable(identity.getCompany())
                                .map(company -> this.getCompanyMapper().toShortResponse(company))
                                .orElse(null)
                )
                .build();
    }
}
