package io.github.bvilii.yapper.common.message.server;

import io.github.bvilii.yapper.common.model.MessageInfo;

import java.util.List;

public record RoomHistory(long roomId, List<MessageInfo> messages) implements ServerMessage {
}
