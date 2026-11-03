package io.github.bvilii.yapper.common.message.server;

public record RegisterFailed(String reason) implements ServerMessage {
}