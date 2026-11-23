package io.github.bvilii.yapper.common.model;

import java.util.List;

/**
 * A room as the client sees it.
 * For DIRECT rooms the name is null: the client shows the other member's name instead.
 */
public record RoomInfo(long id, String name, RoomType type, List<String> members) {

    public RoomInfo {
        members = List.copyOf(members);
    }
}
