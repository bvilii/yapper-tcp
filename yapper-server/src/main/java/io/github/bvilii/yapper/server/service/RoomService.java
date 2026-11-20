package io.github.bvilii.yapper.server.service;

import io.github.bvilii.yapper.common.message.MessageCodec;
import io.github.bvilii.yapper.common.message.client.CreateRoomRequest;
import io.github.bvilii.yapper.common.message.server.CreateRoomFailed;
import io.github.bvilii.yapper.common.message.server.RoomCreated;
import io.github.bvilii.yapper.common.message.server.RoomList;
import io.github.bvilii.yapper.common.model.RoomInfo;
import io.github.bvilii.yapper.server.controller.ClientHandler;
import io.github.bvilii.yapper.server.persistence.Room;
import io.github.bvilii.yapper.server.persistence.RoomRepository;
import io.github.bvilii.yapper.server.persistence.User;
import io.github.bvilii.yapper.server.persistence.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

public class RoomService {
    private static final int MAX_ROOM_NAME_LENGTH = 64;

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final ClientRegistry registry;
    private final MessageCodec codec;

    public RoomService(UserRepository userRepository, RoomRepository roomRepository, ClientRegistry registry, MessageCodec codec) {
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.registry = registry;
        this.codec = codec;
    }

    public void handleRoomCreation(CreateRoomRequest message, ClientHandler sender) {
        User creator = sender.getUser();
        if (creator == null) {
            return;
        }

        String name = message.name() == null ? "" : message.name().trim();
        if (name.isEmpty() || name.length() > MAX_ROOM_NAME_LENGTH) {
            fail(sender, "Room name must be 1-" + MAX_ROOM_NAME_LENGTH + " characters");
            return;
        }

        Set<String> requested = normalizeUsernames(message.usernames(), creator);
        if (requested.isEmpty()) {
            fail(sender, "Add at least one other user");
            return;
        }

        List<User> others = userRepository.findByUsernames(requested);
        List<String> missing = findMissing(requested, others);
        if (!missing.isEmpty()) {
            fail(sender, "User not found: " + String.join(", ", missing));
            return;
        }

        List<User> members = new ArrayList<>();
        members.add(creator);
        members.addAll(others);

        Set<Long> memberIds = members.stream()
                .map(User::id)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        List<String> memberNames = members.stream()
                .map(User::username)
                .toList();

        Room room = roomRepository.create(name, creator.id(), memberIds);

        RoomInfo info = new RoomInfo(room.id(), room.name(), memberNames);
        String json = codec.encode(new RoomCreated(info));
        for (ClientHandler client : registry.getOnlineClients(memberIds)) {
            client.send(json);
        }
    }

    public void handleRoomListRequest(ClientHandler sender) {
        User user = sender.getUser();
        if (user == null) {
            return;
        }

        List<RoomInfo> rooms = roomRepository.findByMemberId(user.id()).stream()
                .map(found -> new RoomInfo(found.room().id(), found.room().name(), found.memberNames()))
                .toList();

        sender.send(codec.encode(new RoomList(rooms)));
    }

    private Set<String> normalizeUsernames(List<String> usernames, User creator) {
        if (usernames == null) {
            return Set.of();
        }

        String ownName = creator.username().toLowerCase(Locale.ROOT);
        return usernames.stream()
                .filter(Objects::nonNull)
                .map(username -> username.trim().toLowerCase(Locale.ROOT))
                .filter(username -> !username.isEmpty())
                .filter(username -> !username.equals(ownName))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private List<String> findMissing(Set<String> requested, List<User> found) {
        Set<String> foundNames = found.stream()
                .map(user -> user.username().toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());

        return requested.stream()
                .filter(username -> !foundNames.contains(username))
                .toList();
    }

    private void fail(ClientHandler sender, String reason) {
        sender.send(codec.encode(new CreateRoomFailed(reason)));
    }
}