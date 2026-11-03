package io.github.bvilii.yapper.common.message.server;

public record LoginFailed(String reason) implements ServerMessage {
}