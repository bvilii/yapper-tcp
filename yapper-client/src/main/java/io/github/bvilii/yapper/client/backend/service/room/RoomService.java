package io.github.bvilii.yapper.client.backend.service.room;

import io.github.bvilii.yapper.client.backend.network.ServerConnection;
import io.github.bvilii.yapper.common.message.MessageCodec;
import io.github.bvilii.yapper.common.message.client.CreateRoomRequest;
import io.github.bvilii.yapper.common.message.server.CreateRoomFailed;
import io.github.bvilii.yapper.common.message.server.RoomCreated;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RoomService implements RoomSubject {
    private final MessageCodec codec;
    private final ServerConnection connection;
    private final List<RoomObserver> observers = new CopyOnWriteArrayList<>();

    public RoomService(MessageCodec codec, ServerConnection connection) {
        this.codec = codec;
        this.connection = connection;
    }

    public void createRoom(String roomName, List<String> usernames) {
        connection.writeToServer(codec.encode(new CreateRoomRequest(roomName, usernames)));
    }

    @Override
    public void addObserver(RoomObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(RoomObserver observer) {
        observers.remove(observer);
    }


    public void handleRoomCreated(RoomCreated message) {
        for (RoomObserver observer : observers) {
            observer.onRoomCreated(message.room());
        }
    }

    public void handleCreateRoomFailed(CreateRoomFailed message) {
        for (RoomObserver observer : observers) {
            observer.onCreateRoomFailed(message.reason());
        }
    }
}
