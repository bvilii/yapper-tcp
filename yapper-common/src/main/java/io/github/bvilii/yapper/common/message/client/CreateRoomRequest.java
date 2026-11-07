package io.github.bvilii.yapper.common.message.client;

import java.util.List;

public record CreateRoomRequest(String name, List<String> usernames) implements ClientMessage {
}