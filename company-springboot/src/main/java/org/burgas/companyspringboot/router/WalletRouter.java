package org.burgas.companyspringboot.router;

import lombok.RequiredArgsConstructor;
import org.burgas.companyspringboot.dto.wallet.WalletRequest;
import org.burgas.companyspringboot.filter.WalletFilterFunction;
import org.burgas.companyspringboot.service.WalletService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class WalletRouter {

    private final WalletService walletService;
    private final WalletFilterFunction walletFilterFunction;

    @Bean
    public RouterFunction<ServerResponse> walletRoutes() {
        return RouterFunctions.route()
                .filter(this.walletFilterFunction)
                .GET(
                        "/api/v1/wallets/by-identity", request -> ServerResponse
                                .status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(
                                        this.walletService.findByIdentity(
                                                UUID.fromString(request.param("identityId").orElseThrow())
                                        )
                                )
                )
                .GET(
                        "/api/v1/wallets/by-id", request -> ServerResponse
                                .status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(
                                        this.walletService.findById(
                                                UUID.fromString(request.param("walletId").orElseThrow())
                                        )
                                )
                )
                .POST(
                        "/api/v1/wallets/create-update", request -> {
                            UUID walletId = this.walletService.createOrUpdate(
                                    (WalletRequest) request.attribute("walletRequest").orElseThrow()
                            );
                            return ServerResponse
                                    .status(HttpStatus.FOUND)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .location(URI.create("/api/v1/wallets/by-id?walletId=" + walletId))
                                    .body(walletId);
                        }
                )
                .DELETE(
                        "/api/v1/wallets/delete", request -> {
                            this.walletService.delete(UUID.fromString(request.param("walletId").orElseThrow()));
                            return ServerResponse.noContent().build();
                        }
                )
                .PUT(
                        "/api/v1/wallets/deposit", request -> {
                            UUID operationId = this.walletService.deposit(
                                    UUID.fromString(request.param("walletId").orElseThrow()),
                                    Double.parseDouble(request.param("amount").orElseThrow())
                            );
                            return ServerResponse
                                    .status(HttpStatus.FOUND)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .location(URI.create("/api/v1/operations/by-id?operationId" + operationId))
                                    .body(operationId);
                        }
                )
                .PUT(
                        "/api/v1/wallets/withdraw", request -> {
                            UUID operationId = this.walletService.withdraw(
                                    UUID.fromString(request.param("walletId").orElseThrow()),
                                    Double.parseDouble(request.param("amount").orElseThrow())
                            );
                            return ServerResponse
                                    .status(HttpStatus.FOUND)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .location(URI.create("/api/v1/operations/by-id?operationId" + operationId))
                                    .body(operationId);
                        }
                )
                .PUT(
                        "/api/v1/wallets/transfer", request -> {
                            UUID operationId = this.walletService.transfer(
                                    UUID.fromString(request.param("senderWalletId").orElseThrow()),
                                    UUID.fromString(request.param("receiverWalletId").orElseThrow()),
                                    Double.parseDouble(request.param("amount").orElseThrow())
                            );
                            return ServerResponse
                                    .status(HttpStatus.FOUND)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .location(URI.create("/api/v1/operations/by-id?operationId" + operationId))
                                    .body(operationId);
                        }
                )
                .build();
    }
}
