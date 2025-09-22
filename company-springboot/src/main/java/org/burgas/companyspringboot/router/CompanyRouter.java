package org.burgas.companyspringboot.router;

import lombok.RequiredArgsConstructor;
import org.burgas.companyspringboot.dto.company.CompanyRequest;
import org.burgas.companyspringboot.service.CompanyService;
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
public class CompanyRouter {

    private final CompanyService companyService;

    @Bean
    public RouterFunction<ServerResponse> companyRoutes() {
        return RouterFunctions.route()
                .GET(
                        "/api/v1/companies", request -> ServerResponse
                                .status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(this.companyService.findAll())
                )
                .GET(
                        "/api/v1/companies/by-id", request -> ServerResponse
                                .status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(
                                        this.companyService.findById(
                                                UUID.fromString(request.param("companyId").orElseThrow())
                                        )
                                )
                )
                .POST(
                        "/api/v1/companies/create-update", request -> {
                            UUID companyId = this.companyService.createOrUpdate(request.body(CompanyRequest.class));
                            return ServerResponse
                                    .status(HttpStatus.FOUND)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .location(URI.create("/api/v1/companies/by-id?companyId=%s".formatted(companyId)))
                                    .body(companyId);
                        }
                )
                .DELETE(
                        "/api/v1/companies/delete", request -> {
                            this.companyService.delete(UUID.fromString(request.param("companyId").orElseThrow()));
                            return ServerResponse.noContent().build();
                        }
                )
                .build();
    }
}
