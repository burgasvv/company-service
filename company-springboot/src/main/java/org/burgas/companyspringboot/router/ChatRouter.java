package org.burgas.companyspringboot.router;

import lombok.RequiredArgsConstructor;
import org.burgas.companyspringboot.dto.chat.ChatRequest;
import org.burgas.companyspringboot.service.ChatService;
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
public class ChatRouter {

    private final ChatService chatService;

    @Bean
    public RouterFunction<ServerResponse> chatRoutes() {
        return RouterFunctions.route()
                .GET(
                        "/api/v1/chats/by-identity", request -> ServerResponse
                                .status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(
                                        this.chatService.findByIdentity(
                                                UUID.fromString(request.param("identityId").orElseThrow())
                                        )
                                )
                )
                .GET(
                        "/api/v1/chats/by-id", request -> ServerResponse
                                .status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(
                                        this.chatService.findById(
                                                UUID.fromString(request.param("chatId").orElseThrow())
                                        )
                                )
                )
                .POST(
                        "/api/v1/chats/create-update", request -> {
                            UUID chatId = this.chatService.createOrUpdate(request.body(ChatRequest.class));
                            return ServerResponse
                                    .status(HttpStatus.FOUND)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .location(URI.create("/api/v1/chats/by-id?chatId=" + chatId))
                                    .body(chatId);
                        }
                )
                .DELETE(
                        "/api/v1/chats/delete", request -> {
                            this.chatService.delete(UUID.fromString(request.param("chatId").orElseThrow()));
                            return ServerResponse.noContent().build();
                        }
                )
                .build();
    }
}
