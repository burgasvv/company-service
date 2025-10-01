package org.burgas.companyspringboot.router;

import lombok.RequiredArgsConstructor;
import org.burgas.companyspringboot.dto.message.MessageRequest;
import org.burgas.companyspringboot.filter.MessageFilterFunction;
import org.burgas.companyspringboot.service.MessageService;
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
public class MessageRouter {

    private final MessageService messageService;
    private final MessageFilterFunction messageFilterFunction;

    @Bean
    public RouterFunction<ServerResponse> messageRoutes() {
        return RouterFunctions.route()
                .filter(this.messageFilterFunction)
                .GET(
                        "/api/v1/messages/by-chat", request -> ServerResponse
                                .status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(
                                        this.messageService.findByChat(
                                                UUID.fromString(request.param("chatId").orElseThrow())
                                        )
                                )
                )
                .GET(
                        "/api/v1/messages/by-id", request -> ServerResponse
                                .status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(
                                        this.messageService.findById(
                                                UUID.fromString(request.param("messageId").orElseThrow())
                                        )
                                )
                )
                .POST(
                        "/api/v1/messages/create-update", request -> {
                            UUID messageId = this.messageService.createOrUpdate(
                                    (MessageRequest) request.attribute("messageRequest").orElseThrow()
                            );
                            return ServerResponse
                                    .status(HttpStatus.FOUND)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .location(URI.create("/api/v1/messages/by-id?messageId=" + messageId))
                                    .body(messageId);
                        }
                )
                .DELETE(
                        "/api/v1/messages/delete", request -> {
                            this.messageService.delete(UUID.fromString(request.param("messageId").orElseThrow()));
                            return ServerResponse.noContent().build();
                        }
                )
                .build();
    }
}
