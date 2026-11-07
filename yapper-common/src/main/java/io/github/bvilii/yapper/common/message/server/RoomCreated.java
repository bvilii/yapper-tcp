package io.github.bvilii.yapper.common.message.server;

import io.github.bvilii.yapper.common.model.RoomInfo;

public record RoomCreated(RoomInfo room) implements ServerMessage {
}