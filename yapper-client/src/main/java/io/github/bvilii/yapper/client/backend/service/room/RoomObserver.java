package io.github.bvilii.yapper.client.backend.service.room;

import io.github.bvilii.yapper.common.model.RoomInfo;

import java.util.List;

public interface RoomObserver {
    default void onRoomCreated(RoomInfo room) {
    }

    default void onCreateRoomFailed(String reason) {
    }

    default void onRoomsLoaded(List<RoomInfo> rooms) {
    }

    default void onDmOpened(RoomInfo room) {
    }

    default void onOpenDmFailed(String reason) {
    }

    default void onRoomDeleted(long roomId) {
    }
}