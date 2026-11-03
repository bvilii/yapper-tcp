package io.github.bvilii.yapper.common.message.server;

public record ErrorMessage(String reason) implements ServerMessage {
}