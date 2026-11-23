package io.github.bvilii.yapper.client.frontend.chat;

import io.github.bvilii.yapper.client.backend.service.auth.Session;
import io.github.bvilii.yapper.common.model.RoomInfo;
import io.github.bvilii.yapper.common.model.RoomType;

/** What to show as a room's title: the room name, or for a DM the other person's name. */
final class RoomTitle {

    private RoomTitle() {
    }

    static String of(RoomInfo room, Session session) {
        if (room.type() == RoomType.DIRECT) {
            for (String member : room.members()) {
                if (!session.isMe(member)) {
                    return member;
                }
            }
        }
        return room.name();
    }
}
