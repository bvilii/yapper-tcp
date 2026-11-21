package io.github.bvilii.yapper.server.service;

import io.github.bvilii.yapper.server.controller.ClientHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ClientRegistry {
    private final Map<Long, ClientHandler> registry;

    public ClientRegistry() {
        this.registry = new ConcurrentHashMap<>();
    }

    public Optional<ClientHandler> register(long userId, ClientHandler handler) {
        return Optional.ofNullable(registry.put(userId, handler))
                .filter(previous -> previous != handler);

        //todo: previous.close();
    }

    public List<ClientHandler> getOnlineClients(Collection<Long> userIds) {
        return userIds.stream()
                .map(registry::get)
                .filter(Objects::nonNull)
                .toList();
    }

    public void unregister(long userId, ClientHandler handler){
        registry.remove(userId, handler);
    }
}
