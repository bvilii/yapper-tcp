package io.github.bvilii.yapper.common.message.server;

import io.github.bvilii.yapper.common.model.RoomInfo;

/** Reply to OpenDmRequest: the direct chat, either the existing one or a new one. */
public record DmOpened(RoomInfo room) implements ServerMessage {
}
