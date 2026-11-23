package io.github.bvilii.yapper.server.persistence;

import io.github.bvilii.yapper.common.model.RoomType;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.*;

public class JdbcRoomRepository implements RoomRepository {
    private static final String INSERT_ROOM =
            "INSERT INTO rooms (name, type, created_by, created_at) VALUES (?, 'GROUP', ?, ?)";
    private static final String INSERT_DIRECT_ROOM =
            "INSERT INTO rooms (name, type, created_by, created_at, dm_key) VALUES (NULL, 'DIRECT', ?, ?, ?)";
    private static final String INSERT_MEMBER =
            "INSERT INTO room_members (room_id, user_id, joined_at) VALUES (?, ?, ?)";
    private static final String FIND_BY_MEMBER_ID = """
            SELECT r.id         AS room_id,
                   r.name       AS room_name,
                   r.type       AS room_type,
                   r.created_by AS created_by,
                   r.created_at AS created_at,
                   u.username   AS member_name
            FROM room_members me
            JOIN rooms r        ON r.id = me.room_id
            JOIN room_members m ON m.room_id = r.id
            JOIN users u        ON u.id = m.user_id
            WHERE me.user_id = ?
            ORDER BY r.created_at DESC, r.id, m.joined_at, u.username
            """;
    private static final String FIND_DIRECT_ROOM = """
            SELECT r.id         AS room_id,
                   r.name       AS room_name,
                   r.type       AS room_type,
                   r.created_by AS created_by,
                   r.created_at AS created_at,
                   u.username   AS member_name
            FROM rooms r
            JOIN room_members m ON m.room_id = r.id
            JOIN users u        ON u.id = m.user_id
            WHERE r.dm_key = ?
            ORDER BY m.joined_at, u.username
            """;
    private static final String FIND_MEMBER_IDS =
            "SELECT user_id FROM room_members WHERE room_id = ?";

    private final DataSource dataSource;

    public JdbcRoomRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Room create(String name, long createdBy, Set<Long> memberIds) {
        Instant createdAt = Instant.now();
        Timestamp timestamp = Timestamp.from(createdAt);

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                long roomId = insertRoom(connection, name, createdBy, timestamp);
                insertMembers(connection, roomId, memberIds, timestamp);
                connection.commit();
                return new Room(roomId, name, RoomType.GROUP, createdBy, createdAt);
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to create room: " + name, e);
        }
    }

    @Override
    public List<RoomWithMembers> findByMemberId(long userId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_MEMBER_ID)) {
            statement.setLong(1, userId);

            Map<Long, Room> rooms = new LinkedHashMap<>();
            Map<Long, List<String>> members = new HashMap<>();

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    long roomId = rs.getLong("room_id");
                    if (!rooms.containsKey(roomId)) {
                        rooms.put(roomId, mapRoom(rs));
                        members.put(roomId, new ArrayList<>());
                    }
                    members.get(roomId).add(rs.getString("member_name"));
                }
            }

            List<RoomWithMembers> result = new ArrayList<>();
            for (Room room : rooms.values()) {
                result.add(new RoomWithMembers(room, members.get(room.id())));
            }
            return result;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find rooms for user: " + userId, e);
        }
    }

    @Override
    public Set<Long> findMemberIds(long roomId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_MEMBER_IDS)) {
            statement.setLong(1, roomId);

            Set<Long> memberIds = new HashSet<>();
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    memberIds.add(rs.getLong("user_id"));
                }
            }
            return memberIds;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find members of room: " + roomId, e);
        }
    }

    @Override
    public Optional<RoomWithMembers> findDirectRoom(long userA, long userB) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_DIRECT_ROOM)) {
            statement.setString(1, dmKey(userA, userB));

            Room room = null;
            List<String> members = new ArrayList<>();
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    if (room == null) {
                        room = mapRoom(rs);
                    }
                    members.add(rs.getString("member_name"));
                }
            }

            if (room == null) {
                return Optional.empty();
            }
            return Optional.of(new RoomWithMembers(room, members));
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find direct room of users: " + userA + ", " + userB, e);
        }
    }

    @Override
    public Room createDirect(long creatorId, long otherId) {
        Instant createdAt = Instant.now();
        Timestamp timestamp = Timestamp.from(createdAt);

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                long roomId = insertDirectRoom(connection, creatorId, otherId, timestamp);
                insertMembers(connection, roomId, Set.of(creatorId, otherId), timestamp);
                connection.commit();
                return new Room(roomId, null, RoomType.DIRECT, creatorId, createdAt);
            } catch (SQLIntegrityConstraintViolationException e) {
                // The UNIQUE dm_key says these two already have a direct room
                connection.rollback();
                throw new DirectRoomAlreadyExistsException(e);
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to create direct room", e);
        }
    }

    private long insertRoom(Connection connection, String name,
                            long createdBy, Timestamp createdAt) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_ROOM, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, name);
            statement.setLong(2, createdBy);
            statement.setTimestamp(3, createdAt);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        }
    }

    private long insertDirectRoom(Connection connection, long creatorId,
                                  long otherId, Timestamp createdAt) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_DIRECT_ROOM, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, creatorId);
            statement.setTimestamp(2, createdAt);
            statement.setString(3, dmKey(creatorId, otherId));
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        }
    }

    private void insertMembers(Connection connection, long roomId,
                               Set<Long> memberIds, Timestamp joinedAt) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_MEMBER)) {
            for (long userId : memberIds) {
                statement.setLong(1, roomId);
                statement.setLong(2, userId);
                statement.setTimestamp(3, joinedAt);
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private Room mapRoom(ResultSet rs) throws SQLException {
        return new Room(
                rs.getLong("room_id"),
                rs.getString("room_name"),
                RoomType.valueOf(rs.getString("room_type")),
                rs.getObject("created_by", Long.class),
                rs.getTimestamp("created_at").toInstant()
        );
    }

    /** The same key for both orders: the smaller user id first, e.g. "3:7". */
    private static String dmKey(long userA, long userB) {
        return Math.min(userA, userB) + ":" + Math.max(userA, userB);
    }
}
