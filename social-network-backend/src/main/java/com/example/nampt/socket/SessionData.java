package com.example.nampt.socket;

import org.springframework.web.socket.WebSocketSession;

public class SessionData {
    private int id;
    private WebSocketSession session;

    public SessionData() {
    }

    public SessionData(int id, WebSocketSession session) {
        this.id = id;
        this.session = session;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public WebSocketSession getSession() {
        return session;
    }

    public void setSession(WebSocketSession session) {
        this.session = session;
    }

    @Override
    public String toString() {
        return "Session{" +
                "id=" + id +
                ", session=" + session +
                '}';
    }
}
