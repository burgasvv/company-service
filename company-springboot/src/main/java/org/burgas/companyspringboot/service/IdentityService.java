package org.burgas.companyspringboot.service;

import lombok.RequiredArgsConstructor;
import org.burgas.companyspringboot.dto.identity.IdentityFullResponse;
import org.burgas.companyspringboot.dto.identity.IdentityRequest;
import org.burgas.companyspringboot.dto.identity.IdentityShortResponse;
import org.burgas.companyspringboot.entity.identity.Identity;
import org.burgas.companyspringboot.exception.IdentityNotFoundException;
import org.burgas.companyspringboot.mapper.IdentityMapper;
import org.burgas.companyspringboot.message.IdentityMessages;
import org.burgas.companyspringboot.repository.IdentityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
public class IdentityService implements CrudService<UUID, IdentityRequest, Identity, IdentityShortResponse, IdentityFullResponse> {

    private final IdentityRepository identityRepository;
    private final IdentityMapper identityMapper;

    @Override
    public Identity findEntity(UUID id) {
        return this.identityRepository.findById(id == null ? UUID.nameUUIDFromBytes(new byte[]{}) : id)
                .orElseThrow(() -> new IdentityNotFoundException(IdentityMessages.IDENTITY_NOT_FOUND.getMessage()));
    }

    @Override
    public List<IdentityShortResponse> findAll() {
        return this.identityRepository.findAll()
                .stream()
                .map(this.identityMapper::toShortResponse)
                .toList();
    }

    @Override
    public IdentityFullResponse findById(UUID id) {
        return this.identityMapper.toFullResponse(this.findEntity(id));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public UUID createOrUpdate(IdentityRequest identityRequest) {
        Identity identity = this.identityMapper.toEntity(identityRequest);
        return this.identityRepository.save(identity).getId();
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void delete(UUID uuid) {
        Identity identity = this.findEntity(uuid);
        this.identityRepository.delete(identity);
    }
}
