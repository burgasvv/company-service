package org.burgas.companyspringboot.filter;

import lombok.RequiredArgsConstructor;
import org.burgas.companyspringboot.entity.chat.Chat;
import org.burgas.companyspringboot.entity.identity.Identity;
import org.burgas.companyspringboot.exception.IdentityNotAuthenticatedException;
import org.burgas.companyspringboot.exception.IdentityNotAuthorizedException;
import org.burgas.companyspringboot.message.IdentityMessages;
import org.burgas.companyspringboot.service.ChatService;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public final class ChatFilterFunction implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private final ChatService chatService;

    @Override
    public @NotNull ServerResponse filter(@NotNull ServerRequest request, @NotNull HandlerFunction<ServerResponse> next) throws Exception {
        if (
                request.path().equals("/api/v1/chats/by-identity")
        ) {
            Authentication authentication = request.principal()
                    .map(Authentication.class::cast)
                    .orElseThrow();

            if (authentication.isAuthenticated()) {
                Identity identity = (Identity) authentication.getPrincipal();
                String identityIdParam = request.param("identityId").orElseThrow();
                UUID identityId = UUID.fromString(identityIdParam);

                if (identityId.equals(identity.getId())) {
                    return next.handle(request);

                } else {
                    throw new IdentityNotAuthorizedException(IdentityMessages.IDENTITY_NOT_AUTHORIZED.getMessage());
                }

            } else {
                throw new IdentityNotAuthenticatedException(IdentityMessages.IDENTITY_NOT_AUTHENTICATED.getMessage());
            }

        } else if (
                request.path().equals("/api/v1/chats/by-id") || request.path().equals("/api/v1/chats/delete")
        ) {
            Authentication authentication = request.principal()
                    .map(Authentication.class::cast)
                    .orElseThrow();

            if (authentication.isAuthenticated()) {
                Identity identity = (Identity) authentication.getPrincipal();
                String chatIdParam = request.param("chatId").orElseThrow();
                UUID chatId = UUID.fromString(chatIdParam);
                Chat chat = this.chatService.findEntity(chatId);

                if (chat.getIdentities().contains(identity)) {
                    return next.handle(request);

                } else {
                    throw new IdentityNotAuthorizedException(IdentityMessages.IDENTITY_NOT_AUTHORIZED.getMessage());
                }

            } else {
                throw new IdentityNotAuthenticatedException(IdentityMessages.IDENTITY_NOT_AUTHENTICATED.getMessage());
            }
        }

        return next.handle(request);
    }
}
