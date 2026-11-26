package io.github.bvilii.yapper.common.message.client;

public record DeleteRoomRequest(long roomId) implements ClientMessage{
}
