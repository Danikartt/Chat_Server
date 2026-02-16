package com.example.servidor_final;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatUserSessionManager {

    private final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    public void addSession(String username, WebSocketSession session) {
        userSessions.put(username, session);
    }

    public void removeSession(String username) {
        userSessions.remove(username);
    }

    public WebSocketSession getSession(String username) {
        return userSessions.get(username);
    }

    public Set<String> getConnectedUsers() {
        return userSessions.keySet();
    }

    public WebSocketSession[] getAllSessions() {
        return userSessions.values().toArray(new WebSocketSession[0]);
    }

}