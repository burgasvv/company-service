package org.burgas.companyspringboot.router;

import lombok.RequiredArgsConstructor;
import org.burgas.companyspringboot.service.OperationService;
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
public class OperationRouter {

    private final OperationService operationService;

    @Bean
    public RouterFunction<ServerResponse> operationRoutes() {
        return RouterFunctions.route()
                .GET(
                        "/api/v1/operations/by-id", request -> ServerResponse
                                .status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(
                                        this.operationService.findById(
                                                UUID.fromString(request.param("operationId").orElseThrow())
                                        )
                                )
                )
                .DELETE(
                        "/api/v1/operations/delete", request -> {
                            this.operationService.delete(UUID.fromString(request.param("operationId").orElseThrow()));
                            return ServerResponse.noContent().build();
                        }
                )
                .build();
    }
}
