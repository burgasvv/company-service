package org.burgas.companyspringboot.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.companyspringboot.dto.chat.ChatFullResponse;
import org.burgas.companyspringboot.dto.chat.ChatRequest;
import org.burgas.companyspringboot.dto.chat.ChatShortResponse;
import org.burgas.companyspringboot.entity.chat.Chat;
import org.burgas.companyspringboot.repository.ChatRepository;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public final class ChatMapper implements EntityMapper<ChatRequest, Chat, ChatShortResponse, ChatFullResponse> {

    private final ChatRepository chatRepository;
    private final ObjectFactory<IdentityMapper> identityMapperObjectFactory;
    private final ObjectFactory<MessageMapper> messageMapperObjectFactory;

    private IdentityMapper getIdentityMapper() {
        return this.identityMapperObjectFactory.getObject();
    }

    private MessageMapper getMessageMapper() {
        return this.messageMapperObjectFactory.getObject();
    }

    @Override
    public Chat toEntity(ChatRequest chatRequest) {
        UUID chatId = this.handleData(chatRequest.getId(), UUID.nameUUIDFromBytes(new byte[]{}));
        return this.chatRepository.findById(chatId)
                .map(
                        chat -> {
                            String name = this.handleData(chatRequest.getName(), chat.getName());
                            String description = this.handleData(chatRequest.getDescription(), chat.getDescription());
                            return Chat.builder()
                                    .id(chat.getId())
                                    .name(name)
                                    .description(description)
                                    .createdAt(chat.getCreatedAt())
                                    .build();
                        }
                )
                .orElseGet(
                        () -> {
                            String name = this.handleDataThrowable(chatRequest.getName(), "");
                            String description = this.handleDataThrowable(chatRequest.getDescription(), "");
                            return Chat.builder()
                                    .name(name)
                                    .description(description)
                                    .createdAt(LocalDateTime.now())
                                    .build();
                        }
                );
    }

    @Override
    public ChatShortResponse toShortResponse(Chat chat) {
        return ChatShortResponse.builder()
                .id(chat.getId())
                .name(chat.getName())
                .description(chat.getDescription())
                .createdAt(chat.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm:ss")))
                .build();
    }

    @Override
    public ChatFullResponse toFullResponse(Chat chat) {
        return ChatFullResponse.builder()
                .id(chat.getId())
                .name(chat.getName())
                .description(chat.getDescription())
                .identities(
                        chat.getIdentities()
                                .stream()
                                .map(identity -> this.getIdentityMapper().toShortResponse(identity))
                                .toList()
                )
                .messages(
                        chat.getMessages()
                                .stream()
                                .map(message -> this.getMessageMapper().toShortResponse(message))
                                .toList()
                )
                .createdAt(chat.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm:ss")))
                .build();
    }
}
