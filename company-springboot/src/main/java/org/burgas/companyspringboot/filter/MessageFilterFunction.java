package org.burgas.companyspringboot.filter;

import lombok.RequiredArgsConstructor;
import org.burgas.companyspringboot.dto.message.MessageRequest;
import org.burgas.companyspringboot.entity.identity.Identity;
import org.burgas.companyspringboot.entity.message.Message;
import org.burgas.companyspringboot.exception.IdentityNotAuthenticatedException;
import org.burgas.companyspringboot.exception.IdentityNotAuthorizedException;
import org.burgas.companyspringboot.message.IdentityMessages;
import org.burgas.companyspringboot.service.MessageService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public final class MessageFilterFunction implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private final ObjectFactory<MessageService> messageServiceObjectFactory;

    private MessageService getMessageService() {
        return messageServiceObjectFactory.getObject();
    }

    @Override
    public @NotNull ServerResponse filter(@NotNull ServerRequest request, @NotNull HandlerFunction<ServerResponse> next) throws Exception {
        if (
                request.path().equals("/api/v1/messages/create-update")
        ) {
            Authentication authentication = request.principal()
                    .map(Authentication.class::cast)
                    .orElseThrow();

            if (authentication.isAuthenticated()) {
                Identity identity = (Identity) authentication.getPrincipal();
                MessageRequest messageRequest = request.body(MessageRequest.class);

                if (identity.getId().equals(messageRequest.getSenderId())) {
                    request.attributes().put("messageRequest", messageRequest);
                    return next.handle(request);

                } else {
                    throw new IdentityNotAuthenticatedException(IdentityMessages.IDENTITY_NOT_AUTHENTICATED.getMessage());
                }

            } else {
                throw new IdentityNotAuthenticatedException(IdentityMessages.IDENTITY_NOT_AUTHENTICATED.getMessage());
            }

        } else if (
                request.path().equals("/api/v1/messages/by-id")
        ) {
            Authentication authentication = request.principal()
                    .map(Authentication.class::cast)
                    .orElseThrow();

            if (authentication.isAuthenticated()) {
                Identity identity = (Identity) authentication.getPrincipal();
                String messageIdParam = request.param("messageId").orElseThrow();
                UUID messageId = UUID.fromString(messageIdParam);
                Message message = this.getMessageService().findEntity(messageId);
                List<Identity> identities = message.getChat().getIdentities();

                for (Identity chatIdentity : identities) {

                    if (chatIdentity.getId().equals(identity.getId()))
                        return next.handle(request);
                }

                throw new IdentityNotAuthorizedException(IdentityMessages.IDENTITY_NOT_AUTHORIZED.getMessage());

            } else {
                throw new IdentityNotAuthenticatedException(IdentityMessages.IDENTITY_NOT_AUTHENTICATED.getMessage());
            }

        } else if (
                request.path().equals("/api/v1/messages/delete")
        ) {
            Authentication authentication = request.principal()
                    .map(Authentication.class::cast)
                    .orElseThrow();

            if (authentication.isAuthenticated()) {
                Identity identity = (Identity) authentication.getPrincipal();
                String messageIdParam = request.param("messageId").orElseThrow();
                UUID messageId = UUID.fromString(messageIdParam);
                Message message = this.getMessageService().findEntity(messageId);

                if (message.getSender().getId().equals(identity.getId())) {
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
