package io.github.bvilii.yapper.common.message.client;

public record RegisterRequest(String username, String password, String email) implements ClientMessage {
}