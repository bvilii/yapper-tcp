package io.github.bvilii.yapper.client.backend.service.room;

import io.github.bvilii.yapper.common.model.RoomInfo;

public interface RoomObserver {
    default void onRoomCreated(RoomInfo room) {
    }

    default void onCreateRoomFailed(String reason) {
    }
}
