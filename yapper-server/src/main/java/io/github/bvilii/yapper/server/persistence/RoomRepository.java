package io.github.bvilii.yapper.server.persistence;

import java.util.Set;

public interface RoomRepository {

    Room create(String name, long createdBy, Set<Long> memberIds);
}