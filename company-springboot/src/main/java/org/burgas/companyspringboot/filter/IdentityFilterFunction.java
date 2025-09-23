package org.burgas.companyspringboot.filter;

import org.burgas.companyspringboot.dto.identity.IdentityRequest;
import org.burgas.companyspringboot.entity.identity.Identity;
import org.burgas.companyspringboot.exception.IdentityNotAuthenticatedException;
import org.burgas.companyspringboot.exception.IdentityNotAuthorizedException;
import org.burgas.companyspringboot.message.IdentityMessages;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.UUID;

@Component
public class IdentityFilterFunction implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    @Override
    public @NotNull ServerResponse filter(@NotNull ServerRequest request, @NotNull HandlerFunction<ServerResponse> next) throws Exception {
        if (
                request.path().equals("/api/v1/identities/update")
        ) {
            Authentication authentication = request.principal()
                    .map(Authentication.class::cast)
                    .orElseThrow();

            if (authentication.isAuthenticated()) {
                Identity identity = (Identity) authentication.getPrincipal();
                IdentityRequest identityRequest = request.body(IdentityRequest.class);

                if (identity.getId().equals(identityRequest.getId())) {
                    request.attributes().put("identityRequest", identityRequest);
                    return next.handle(request);

                } else {
                    throw new IdentityNotAuthorizedException(IdentityMessages.IDENTITY_NOT_AUTHORIZED.getMessage());
                }

            } else {
                throw new IdentityNotAuthenticatedException(IdentityMessages.IDENTITY_NOT_AUTHENTICATED.getMessage());
            }

        } else if (
                request.path().equals("/api/v1/identities/delete") ||
                request.path().equals("/api/v1/identities/add-company")
        ) {
            Authentication authentication = request.principal()
                    .map(Authentication.class::cast)
                    .orElseThrow();

            if (authentication.isAuthenticated()) {
                Identity identity = (Identity) authentication.getPrincipal();
                String identityIdParam = request.param("identityId").orElseThrow();
                UUID identityId = UUID.fromString(identityIdParam);

                if (identity.getId().equals(identityId)) {
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
