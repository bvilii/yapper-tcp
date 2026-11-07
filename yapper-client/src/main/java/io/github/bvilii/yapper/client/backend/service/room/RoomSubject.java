package io.github.bvilii.yapper.client.backend.service.room;

public interface RoomSubject {
    void addObserver(RoomObserver observer);
    void removeObserver(RoomObserver observer);
}
