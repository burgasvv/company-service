package org.burgas.companyspringboot.filter;

import lombok.RequiredArgsConstructor;
import org.burgas.companyspringboot.entity.identity.Identity;
import org.burgas.companyspringboot.entity.operation.Operation;
import org.burgas.companyspringboot.exception.IdentityNotAuthenticatedException;
import org.burgas.companyspringboot.exception.IdentityNotAuthorizedException;
import org.burgas.companyspringboot.message.IdentityMessages;
import org.burgas.companyspringboot.service.OperationService;
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
public final class OperationFilterFunction implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private final OperationService operationService;

    @Override
    public @NotNull ServerResponse filter(@NotNull ServerRequest request, @NotNull HandlerFunction<ServerResponse> next) throws Exception {
        if (
                request.path().equals("/api/v1/operations/by-id")
        ) {
            Authentication authentication = request.principal()
                    .map(Authentication.class::cast)
                    .orElseThrow();

            if (authentication.isAuthenticated()) {
                Identity identity = (Identity) authentication.getPrincipal();
                String operationIdParam = request.param("operationId").orElseThrow();
                UUID operationId = UUID.fromString(operationIdParam);
                Operation operation = this.operationService.findEntity(operationId);
                Identity senderIdentity = operation.getSenderWallet().getIdentity();
                Identity receiverIdentity = operation.getReceiverWallet().getIdentity();

                if (
                        (identity.getId().equals(senderIdentity.getId()) ||
                        identity.getId().equals(receiverIdentity.getId())) ||
                        (identity.getId().equals(senderIdentity.getId()) &&
                         identity.getId().equals(receiverIdentity.getId()))
                ) {
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
