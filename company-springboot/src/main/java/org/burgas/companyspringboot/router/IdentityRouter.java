package org.burgas.companyspringboot.router;

import lombok.RequiredArgsConstructor;
import org.burgas.companyspringboot.dto.identity.IdentityRequest;
import org.burgas.companyspringboot.service.IdentityService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class IdentityRouter {

    private final IdentityService identityService;

    @Bean
    public RouterFunction<ServerResponse> identityRoutes() {
        return RouterFunctions.route()
                .GET(
                        "/api/v1/identities", request -> ServerResponse
                                .status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(this.identityService.findAll())
                )
                .GET(
                        "/api/v1/identities/by-id", request -> ServerResponse
                                .status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(
                                        this.identityService.findById(
                                                UUID.fromString(request.param("identityId").orElseThrow())
                                        )
                                )
                )
                .POST(
                        "/api/v1/identities/create", request -> {
                            UUID identityId = this.identityService.createOrUpdate(request.body(IdentityRequest.class));
                            return ServerResponse
                                    .status(HttpStatus.OK)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(identityId);
                        }
                )
                .PUT(
                        "/api/v1/identities/update", request -> {
                            UUID identityId = this.identityService.createOrUpdate(request.body(IdentityRequest.class));
                            return ServerResponse
                                    .status(HttpStatus.OK)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(identityId);
                        }
                )
                .DELETE(
                        "/api/v1/identities/delete", request -> {
                            this.identityService.delete(UUID.fromString(request.param("identityId").orElseThrow()));
                            return ServerResponse.noContent().build();
                        }
                )
                .build();
    }
}
