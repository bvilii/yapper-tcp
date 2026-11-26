package io.github.bvilii.yapper.server.persistence;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoomRepository {

    Room create(String name, long createdBy, Set<Long> memberIds);

    List<RoomWithMembers> findByMemberId(long userId);

    Set<Long> findMemberIds(long roomId);

    Optional<RoomWithMembers> findDirectRoom(long userA, long userB);

    /** Creates the direct room of two users. Throws DirectRoomAlreadyExistsException if they already have one. */
    Room createDirect(long creatorId, long otherId);

    void deleteRoomByRoomId(long roomId);
}
