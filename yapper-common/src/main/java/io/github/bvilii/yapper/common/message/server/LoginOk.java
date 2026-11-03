package io.github.bvilii.yapper.common.message.server;

public record LoginOk(String username, String token) implements ServerMessage {
}