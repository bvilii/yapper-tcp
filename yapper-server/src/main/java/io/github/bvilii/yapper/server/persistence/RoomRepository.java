package io.github.bvilii.yapper.server.persistence;

import java.util.List;
import java.util.Set;

public interface RoomRepository {

    Room create(String name, long createdBy, Set<Long> memberIds);

    List<RoomWithMembers> findByMemberId(long userId);

    Set<Long> findMemberIds(long roomId);
}