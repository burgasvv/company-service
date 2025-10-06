package org.burgas.companyspringboot.handler;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.HtmlUtils;

@Configuration
public class ServerWebSocketHandler extends TextWebSocketHandler {

    @Override
    protected void handleTextMessage(@NotNull WebSocketSession session, @NotNull TextMessage message) throws Exception {
        String request = message.getPayload();
        String response = String.format("response from server to '%s'", HtmlUtils.htmlEscape(request));
        session.sendMessage(new TextMessage(response));
    }
}
