package io.github.bvilii.yapper.server.service;

import io.github.bvilii.yapper.common.message.MessageCodec;
import io.github.bvilii.yapper.common.message.client.CreateRoomRequest;
import io.github.bvilii.yapper.common.message.client.OpenDmRequest;
import io.github.bvilii.yapper.common.message.server.CreateRoomFailed;
import io.github.bvilii.yapper.common.message.server.DmOpened;
import io.github.bvilii.yapper.common.message.server.OpenDmFailed;
import io.github.bvilii.yapper.common.message.server.RoomCreated;
import io.github.bvilii.yapper.common.message.server.RoomList;
import io.github.bvilii.yapper.common.model.RoomInfo;
import io.github.bvilii.yapper.common.model.RoomType;
import io.github.bvilii.yapper.server.controller.ClientHandler;
import io.github.bvilii.yapper.server.persistence.DirectRoomAlreadyExistsException;
import io.github.bvilii.yapper.server.persistence.Room;
import io.github.bvilii.yapper.server.persistence.RoomRepository;
import io.github.bvilii.yapper.server.persistence.RoomWithMembers;
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

        RoomInfo info = new RoomInfo(room.id(), room.name(), room.type(), memberNames);
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
                .map(this::toRoomInfo)
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

    public void handleOpenDm(OpenDmRequest message, ClientHandler sender) {
        User me = sender.getUser();
        if (me == null) {
            return;
        }

        String username = message.username() == null ? "" : message.username().trim();
        if (username.isEmpty()) {
            failDm(sender, "Type a username");
            return;
        }
        if (username.equalsIgnoreCase(me.username())) {
            failDm(sender, "You can't start a chat with yourself");
            return;
        }

        Optional<User> found = userRepository.findByUsername(username);
        if (found.isEmpty()) {
            failDm(sender, "User not found: " + username);
            return;
        }
        User other = found.get();

        // We already have a DM with this person: just open it
        Optional<RoomWithMembers> existing = roomRepository.findDirectRoom(me.id(), other.id());
        if (existing.isPresent()) {
            sender.send(codec.encode(new DmOpened(toRoomInfo(existing.get()))));
            return;
        }

        Room room;
        try {
            room = roomRepository.createDirect(me.id(), other.id());
        } catch (DirectRoomAlreadyExistsException e) {
            // The other person opened the same DM at the same moment: use that one
            RoomWithMembers created = roomRepository.findDirectRoom(me.id(), other.id()).orElseThrow();
            sender.send(codec.encode(new DmOpened(toRoomInfo(created))));
            return;
        }

        RoomInfo info = new RoomInfo(room.id(), null, RoomType.DIRECT, List.of(me.username(), other.username()));
        sender.send(codec.encode(new DmOpened(info)));

        // For the other person this is a brand-new room in their list
        String roomCreated = codec.encode(new RoomCreated(info));
        for (ClientHandler client : registry.getOnlineClients(List.of(other.id()))) {
            client.send(roomCreated);
        }
    }

    private RoomInfo toRoomInfo(RoomWithMembers found) {
        Room room = found.room();
        return new RoomInfo(room.id(), room.name(), room.type(), found.memberNames());
    }

    private void failDm(ClientHandler sender, String reason) {
        sender.send(codec.encode(new OpenDmFailed(reason)));
    }
}