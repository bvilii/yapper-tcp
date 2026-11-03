package io.github.bvilii.yapper.common.message.client;

public record LoginRequest(String username, String password) implements ClientMessage {
}