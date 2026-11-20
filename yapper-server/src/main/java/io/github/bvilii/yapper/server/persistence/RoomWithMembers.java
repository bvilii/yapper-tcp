package io.github.bvilii.yapper.server.persistence;

import java.util.List;

public record RoomWithMembers(Room room, List<String> memberNames) {

    public RoomWithMembers {
        memberNames = List.copyOf(memberNames);
    }
}