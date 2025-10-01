package org.burgas.companyspringboot.service;

import lombok.RequiredArgsConstructor;
import org.burgas.companyspringboot.dto.identity.IdentityFullResponse;
import org.burgas.companyspringboot.dto.identity.IdentityRequest;
import org.burgas.companyspringboot.dto.identity.IdentityShortResponse;
import org.burgas.companyspringboot.entity.chat.Chat;
import org.burgas.companyspringboot.entity.company.Company;
import org.burgas.companyspringboot.entity.identity.Identity;
import org.burgas.companyspringboot.exception.EmptyPasswordException;
import org.burgas.companyspringboot.exception.IdentityNotFoundException;
import org.burgas.companyspringboot.exception.MatchesPasswordException;
import org.burgas.companyspringboot.mapper.IdentityMapper;
import org.burgas.companyspringboot.message.IdentityMessages;
import org.burgas.companyspringboot.repository.IdentityRepository;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final ObjectFactory<CompanyService> companyServiceObjectFactory;
    private final PasswordEncoder passwordEncoder;
    private final ObjectFactory<ChatService> chatServiceObjectFactory;

    public CompanyService getCompanyService() {
        return this.companyServiceObjectFactory.getObject();
    }

    public ChatService getChatService() {
        return this.chatServiceObjectFactory.getObject();
    }

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

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void addCompany(final UUID companyId, final UUID identityId) {
        Identity identity = this.findEntity(identityId);
        Company company = this.getCompanyService().findEntity(companyId);
        identity.setCompany(company);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void changePassword(final UUID identityId, final String newPassword) {
        if (newPassword != null && !newPassword.isBlank()) {
            Identity identity = this.findEntity(identityId);

            if (this.passwordEncoder.matches(newPassword, identity.getPassword()))
                throw new MatchesPasswordException(IdentityMessages.MATCHES_PASSWORD.getMessage());

            identity.setPassword(this.passwordEncoder.encode(newPassword));

        } else {
            throw new EmptyPasswordException(IdentityMessages.EMPTY_PASSWORD.getMessage());
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void addChat(final UUID identityId, final UUID chatId) {
        Identity identity = this.findEntity(identityId);
        Chat chat = this.getChatService().findEntity(chatId);
        identity.addChat(chat);
    }
}
