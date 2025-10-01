package org.burgas.companyspringboot.filter;

import lombok.RequiredArgsConstructor;
import org.burgas.companyspringboot.dto.wallet.WalletRequest;
import org.burgas.companyspringboot.entity.identity.Identity;
import org.burgas.companyspringboot.entity.wallet.Wallet;
import org.burgas.companyspringboot.exception.IdentityNotAuthenticatedException;
import org.burgas.companyspringboot.exception.IdentityNotAuthorizedException;
import org.burgas.companyspringboot.message.IdentityMessages;
import org.burgas.companyspringboot.service.WalletService;
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
public final class WalletFilterFunction implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private final WalletService walletService;

    @Override
    public @NotNull ServerResponse filter(@NotNull ServerRequest request, @NotNull HandlerFunction<ServerResponse> next) throws Exception {
        if (
                request.path().equals("/api/v1/wallets/by-id") ||
                request.path().equals("/api/v1/wallets/deposit") ||
                request.path().equals("/api/v1/wallets/withdraw") ||
                request.path().equals("/api/v1/wallets/delete")
        ) {
            Authentication authentication = request.principal()
                    .map(Authentication.class::cast)
                    .orElseThrow();

            if (authentication.isAuthenticated()) {
                Identity identity = (Identity) authentication.getPrincipal();
                String walletIdParam = request.param("walletId").orElseThrow();
                UUID walletId = UUID.fromString(walletIdParam);
                Wallet wallet = this.walletService.findEntity(walletId);

                if (wallet.getIdentity().getId().equals(identity.getId())) {
                    return next.handle(request);

                } else {
                    throw new IdentityNotAuthorizedException(IdentityMessages.IDENTITY_NOT_AUTHORIZED.getMessage());
                }

            } else {
                throw new IdentityNotAuthenticatedException(IdentityMessages.IDENTITY_NOT_AUTHENTICATED.getMessage());
            }

        } else if (
                request.path().equals("/api/v1/wallets/by-identity")
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

        } else if (
                request.path().equals("/api/v1/wallets/create-update")
        ) {
            Authentication authentication = request.principal()
                    .map(Authentication.class::cast)
                    .orElseThrow();

            if (authentication.isAuthenticated()) {
                Identity identity = (Identity) authentication.getPrincipal();
                WalletRequest walletRequest = request.body(WalletRequest.class);

                if (walletRequest.getIdentityId().equals(identity.getId())) {
                    request.attributes().put("walletRequest", walletRequest);
                    return next.handle(request);

                } else {
                    throw new IdentityNotAuthorizedException(IdentityMessages.IDENTITY_NOT_AUTHORIZED.getMessage());
                }

            } else {
                throw new IdentityNotAuthenticatedException(IdentityMessages.IDENTITY_NOT_AUTHENTICATED.getMessage());
            }

        } else if (
                request.path().equals("/api/v1/wallets/transfer")
        ) {
            Authentication authentication = request.principal()
                    .map(Authentication.class::cast)
                    .orElseThrow();

            if (authentication.isAuthenticated()) {
                Identity identity = (Identity) authentication.getPrincipal();
                String senderWalletIdParam = request.param("senderWalletId").orElseThrow();
                UUID senderWalletId = UUID.fromString(senderWalletIdParam);
                Wallet wallet = this.walletService.findEntity(senderWalletId);

                if (wallet.getIdentity().getId().equals(identity.getId())) {
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
