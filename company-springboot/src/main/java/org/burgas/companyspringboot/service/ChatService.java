package org.burgas.companyspringboot.service;

import lombok.RequiredArgsConstructor;
import org.burgas.companyspringboot.dto.chat.ChatFullResponse;
import org.burgas.companyspringboot.dto.chat.ChatRequest;
import org.burgas.companyspringboot.dto.chat.ChatShortResponse;
import org.burgas.companyspringboot.entity.chat.Chat;
import org.burgas.companyspringboot.entity.identity.Identity;
import org.burgas.companyspringboot.exception.ChatNotFoundException;
import org.burgas.companyspringboot.mapper.ChatMapper;
import org.burgas.companyspringboot.message.ChatMessages;
import org.burgas.companyspringboot.repository.ChatRepository;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatMapper chatMapper;

    private final ObjectFactory<IdentityService> identityServiceObjectFactory;

    private IdentityService getIdentityService() {
        return this.identityServiceObjectFactory.getObject();
    }

    public Chat findEntity(final UUID chatId) {
        return this.chatRepository.findById(chatId == null ? UUID.randomUUID() : chatId)
                .orElseThrow(() -> new ChatNotFoundException(ChatMessages.CHAT_NOT_FOUND.getMessage()));
    }

    public List<ChatShortResponse> findByIdentity(final UUID identityId) {
        Identity identity = this.getIdentityService().findEntity(identityId);
        return identity.getChats()
                .stream()
                .map(this.chatMapper::toShortResponse)
                .toList();
    }

    public ChatFullResponse findById(final UUID chatId) {
        return this.chatMapper.toFullResponse(this.findEntity(chatId));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public UUID createOrUpdate(final ChatRequest chatRequest) {
        Chat chat = this.chatMapper.toEntity(chatRequest);
        return this.chatRepository.save(chat).getId();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void delete(final UUID chatId) {
        Chat chat = this.findEntity(chatId);
        this.chatRepository.delete(chat);
    }
}
