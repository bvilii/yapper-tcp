package io.github.bvilii.yapper.server.persistence;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class JdbcMessageRepository implements MessageRepository {
    private static final String INSERT =
            "INSERT INTO messages (room_id, sender_id, content, created_at) VALUES (?, ?, ?, ?)";
    private static final String FIND_LATEST_BY_ROOM = """
            SELECT id, room_id, sender_id, sender_name, content, created_at
            FROM (
                SELECT m.id,
                       m.room_id,
                       m.sender_id,
                       u.username AS sender_name,
                       m.content,
                       m.created_at
                FROM messages m
                LEFT JOIN users u ON u.id = m.sender_id
                WHERE m.room_id = ?
                ORDER BY m.id DESC
                LIMIT ?
            ) latest
            ORDER BY id
            """;

    private final DataSource dataSource;

    public JdbcMessageRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Message save(long roomId, long senderId, String content) {
        Instant createdAt = Instant.now();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, roomId);
            statement.setLong(2, senderId);
            statement.setString(3, content);
            statement.setTimestamp(4, Timestamp.from(createdAt));
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                long id = keys.getLong(1);
                return new Message(id, roomId, senderId, content, createdAt);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to save message in room: " + roomId, e);
        }
    }

    @Override
    public List<MessageWithSender> findLatestByRoom(long roomId, int limit) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_LATEST_BY_ROOM)) {
            statement.setLong(1, roomId);
            statement.setInt(2, limit);

            List<MessageWithSender> messages = new ArrayList<>();
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Message message = new Message(
                            rs.getLong("id"),
                            rs.getLong("room_id"),
                            rs.getObject("sender_id", Long.class),
                            rs.getString("content"),
                            rs.getTimestamp("created_at").toInstant()
                    );
                    messages.add(new MessageWithSender(message, rs.getString("sender_name")));
                }
            }
            return messages;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to load messages of room: " + roomId, e);
        }
    }
}