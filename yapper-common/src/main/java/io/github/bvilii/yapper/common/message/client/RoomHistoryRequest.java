package io.github.bvilii.yapper.common.message.client;

public record RoomHistoryRequest(long roomId) implements ClientMessage {
}
