package io.github.bvilii.yapper.client.backend.service.auth;

public class Session {
    private volatile String username;

    public void start(String username) {
        this.username = username;
    }

    public void clear() {
        this.username = null;
    }

    public String username() {
        return username;
    }

    public boolean isMe(String name) {
        String current = username;
        return current != null && current.equals(name);
    }
}