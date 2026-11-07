package io.github.bvilii.yapper.common.message.server;

public record CreateRoomFailed(String reason) implements ServerMessage {
}