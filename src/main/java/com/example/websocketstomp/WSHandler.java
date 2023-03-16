package com.example.websocketstomp;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WSHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String newSessionId = session.getId();
        sessions.put(newSessionId,session);
        sessions.values().forEach(s->{
            try {
                s.sendMessage(new TextMessage(newSessionId+" is connected"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        sessions.values().forEach(s->{
            try {
                s.sendMessage(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String closedSessionId = session.getId();
        sessions.remove(closedSessionId);
        sessions.values().forEach(s->{
            try {
                s.sendMessage(new TextMessage(closedSessionId+" is closed"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
    }
}
