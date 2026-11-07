package io.github.bvilii.yapper.common.model;

import java.util.List;
import java.util.Objects;

public record RoomInfo(long id, String name, List<String> members) {

    public RoomInfo {
        Objects.requireNonNull(name, "name must not be null");
        members = List.copyOf(members);
    }
}