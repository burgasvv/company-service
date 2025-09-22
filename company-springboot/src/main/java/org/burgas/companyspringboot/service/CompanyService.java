package org.burgas.companyspringboot.service;

import lombok.RequiredArgsConstructor;
import org.burgas.companyspringboot.dto.company.CompanyFullResponse;
import org.burgas.companyspringboot.dto.company.CompanyRequest;
import org.burgas.companyspringboot.dto.company.CompanyShortResponse;
import org.burgas.companyspringboot.entity.company.Company;
import org.burgas.companyspringboot.exception.CompanyNotFoundException;
import org.burgas.companyspringboot.mapper.CompanyMapper;
import org.burgas.companyspringboot.message.CompanyMessages;
import org.burgas.companyspringboot.repository.CompanyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
public class CompanyService implements CrudService<UUID, CompanyRequest, Company, CompanyShortResponse, CompanyFullResponse> {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    @Override
    public Company findEntity(UUID id) {
        return this.companyRepository.findById(id == null ? UUID.nameUUIDFromBytes(new byte[]{}) : id)
                .orElseThrow(() -> new CompanyNotFoundException(CompanyMessages.COMPANY_NOT_FOUND.getMessage()));
    }

    @Override
    public List<CompanyShortResponse> findAll() {
        return this.companyRepository.findAll()
                .stream()
                .map(this.companyMapper::toShortResponse)
                .toList();
    }

    @Override
    public CompanyFullResponse findById(UUID id) {
        return this.companyMapper.toFullResponse(this.findEntity(id));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public UUID createOrUpdate(CompanyRequest companyRequest) {
        Company company = this.companyMapper.toEntity(companyRequest);
        return this.companyRepository.save(company).getId();
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void delete(UUID uuid) {
        Company company = this.findEntity(uuid);
        this.companyRepository.delete(company);
    }
}
