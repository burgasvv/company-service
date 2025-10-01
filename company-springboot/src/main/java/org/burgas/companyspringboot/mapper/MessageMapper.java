package org.burgas.companyspringboot.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.companyspringboot.dto.message.MessageFullResponse;
import org.burgas.companyspringboot.dto.message.MessageRequest;
import org.burgas.companyspringboot.dto.message.MessageShortResponse;
import org.burgas.companyspringboot.entity.chat.Chat;
import org.burgas.companyspringboot.entity.identity.Identity;
import org.burgas.companyspringboot.entity.message.Message;
import org.burgas.companyspringboot.exception.IdentityNotInChatException;
import org.burgas.companyspringboot.message.IdentityMessages;
import org.burgas.companyspringboot.repository.ChatRepository;
import org.burgas.companyspringboot.repository.IdentityRepository;
import org.burgas.companyspringboot.repository.MessageRepository;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

import static org.burgas.companyspringboot.message.ChatMessages.*;

@Component
@RequiredArgsConstructor
public final class MessageMapper implements EntityMapper<MessageRequest, Message, MessageShortResponse, MessageFullResponse> {

    private final MessageRepository messageRepository;
    private final ObjectFactory<IdentityRepository> identityRepositoryObjectFactory;
    private final ObjectFactory<IdentityMapper> identityMapperObjectFactory;
    private final ObjectFactory<ChatRepository> chatRepositoryObjectFactory;
    private final ObjectFactory<ChatMapper> chatMapperObjectFactory;

    private IdentityRepository getIdentityRepository() {
        return this.identityRepositoryObjectFactory.getObject();
    }

    private IdentityMapper getIdentityMapper() {
        return this.identityMapperObjectFactory.getObject();
    }

    private ChatRepository getChatRepository() {
        return this.chatRepositoryObjectFactory.getObject();
    }

    private ChatMapper getChatMapper() {
        return this.chatMapperObjectFactory.getObject();
    }

    @Override
    public Message toEntity(MessageRequest messageRequest) {
        UUID messageId = this.handleData(messageRequest.getId(), UUID.nameUUIDFromBytes(new byte[]{}));
        return this.messageRepository.findById(messageId)
                .map(
                        message -> {
                            String content = this.handleData(messageRequest.getContent(), message.getContent());
                            UUID senderId = this.handleData(messageRequest.getSenderId(), UUID.nameUUIDFromBytes(new byte[]{}));
                            Identity sender = this.handleData(
                                    this.getIdentityRepository().findById(senderId).orElse(null),
                                    message.getSender()
                            );
                            UUID chatId = this.handleData(messageRequest.getChatId(), UUID.nameUUIDFromBytes(new byte[]{}));
                            Chat chat = this.handleData(
                                    this.getChatRepository().findById(chatId).orElse(null),
                                    message.getChat()
                            );
                            if (!chat.getIdentities().contains(sender))
                                throw new IdentityNotInChatException(IdentityMessages.IDENTITY_NOT_IN_CHAT.getMessage());

                            return Message.builder()
                                    .id(message.getId())
                                    .content(content)
                                    .sender(sender)
                                    .chat(chat)
                                    .createdAt(message.getCreatedAt())
                                    .build();
                        }
                )
                .orElseGet(
                        () -> {
                            String content = this.handleDataThrowable(messageRequest.getContent(),
                                    CONTENT_FIELD_EMPTY.getMessage());
                            UUID senderId = this.handleData(messageRequest.getSenderId(), UUID.nameUUIDFromBytes(new byte[]{}));
                            Identity sender = this.handleDataThrowable(
                                    this.getIdentityRepository().findById(senderId).orElse(null),
                                    SENDER_FIELD_EMPTY.getMessage()
                            );
                            UUID chatId = this.handleData(messageRequest.getChatId(), UUID.nameUUIDFromBytes(new byte[]{}));
                            Chat chat = this.handleDataThrowable(
                                    this.getChatRepository().findById(chatId).orElse(null),
                                    CHAT_FIELD_EMPTY.getMessage()
                            );
                            if (!chat.getIdentities().contains(sender))
                                throw new IdentityNotInChatException(IdentityMessages.IDENTITY_NOT_IN_CHAT.getMessage());

                            return Message.builder()
                                    .content(content)
                                    .sender(sender)
                                    .chat(chat)
                                    .createdAt(LocalDateTime.now())
                                    .build();
                        }
                );
    }

    @Override
    public MessageShortResponse toShortResponse(Message message) {
        return MessageShortResponse.builder()
                .id(message.getId())
                .content(message.getContent())
                .sender(
                        Optional.ofNullable(message.getSender())
                                .map(identity -> this.getIdentityMapper().toShortResponse(identity))
                                .orElse(null)
                )
                .createdAt(message.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm:ss")))
                .build();
    }

    @Override
    public MessageFullResponse toFullResponse(Message message) {
        return MessageFullResponse.builder()
                .id(message.getId())
                .content(message.getContent())
                .chat(
                        Optional.ofNullable(message.getChat())
                                .map(chat -> this.getChatMapper().toShortResponse(chat))
                                .orElse(null)
                )
                .sender(
                        Optional.ofNullable(message.getSender())
                                .map(identity -> this.getIdentityMapper().toShortResponse(identity))
                                .orElse(null)
                )
                .createdAt(message.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm:ss")))
                .build();
    }
}
