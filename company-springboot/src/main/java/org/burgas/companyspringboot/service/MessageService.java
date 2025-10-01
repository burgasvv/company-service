package org.burgas.companyspringboot.service;

import lombok.RequiredArgsConstructor;
import org.burgas.companyspringboot.dto.message.MessageFullResponse;
import org.burgas.companyspringboot.dto.message.MessageRequest;
import org.burgas.companyspringboot.dto.message.MessageShortResponse;
import org.burgas.companyspringboot.entity.chat.Chat;
import org.burgas.companyspringboot.entity.message.Message;
import org.burgas.companyspringboot.exception.MessageNotFoundException;
import org.burgas.companyspringboot.mapper.MessageMapper;
import org.burgas.companyspringboot.message.ChatMessages;
import org.burgas.companyspringboot.repository.MessageRepository;
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
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final ObjectFactory<ChatService> chatServiceObjectFactory;

    public ChatService getChatService() {
        return this.chatServiceObjectFactory.getObject();
    }

    public Message findEntity(final UUID messageId) {
        return this.messageRepository.findById(messageId == null ? UUID.randomUUID() : messageId)
                .orElseThrow(() -> new MessageNotFoundException(ChatMessages.MESSAGE_NOT_FOUND.getMessage()));
    }

    public List<MessageShortResponse> findByChat(final UUID chatId) {
        Chat chat = this.getChatService().findEntity(chatId);
        return chat.getMessages()
                .stream()
                .map(this.messageMapper::toShortResponse)
                .toList();
    }

    public MessageFullResponse findById(final UUID messageId) {
        return this.messageMapper.toFullResponse(this.findEntity(messageId));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public UUID createOrUpdate(final MessageRequest messageRequest) {
        Message message = this.messageMapper.toEntity(messageRequest);
        return this.messageRepository.save(message).getId();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void delete(final UUID messageId) {
        Message message = this.findEntity(messageId);
        this.messageRepository.delete(message);
    }
}
