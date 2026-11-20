package io.github.bvilii.yapper.common.message.server;

import io.github.bvilii.yapper.common.model.RoomInfo;

import java.util.List;

public record RoomList(List<RoomInfo> rooms) implements ServerMessage {
}