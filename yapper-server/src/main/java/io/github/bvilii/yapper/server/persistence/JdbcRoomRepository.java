package io.github.bvilii.yapper.server.persistence;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.Set;

public class JdbcRoomRepository implements RoomRepository {
    private static final String INSERT_ROOM = "INSERT INTO rooms (name, created_by, created_at) VALUES (?, ?, ?)";
    private static final String INSERT_MEMBER = "INSERT INTO room_members (room_id, user_id, joined_at) VALUES (?, ?, ?)";

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
                return new Room(roomId, name, createdBy, createdAt);
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
}