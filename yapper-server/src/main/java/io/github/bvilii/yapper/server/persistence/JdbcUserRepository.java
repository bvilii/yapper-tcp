package io.github.bvilii.yapper.server.persistence;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.Optional;

public class JdbcUserRepository implements UserRepository {
    private static final String FIND_BY_USERNAME = "SELECT id, username, email, password_hash, created_at FROM users WHERE username = ?";
    private static final String INSERT = "INSERT INTO users (username, email, password_hash, created_at) VALUES (?, ?, ?, ?)";

    private final DataSource dataSource;

    public JdbcUserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_USERNAME)) {
            statement.setString(1, username);

            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapUser(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find user: " + username, e);
        }
    }

    @Override
    public User save(String username, String email, String passwordHash) {
        Instant createdAt = Instant.now();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, username);
            statement.setString(2, email);
            statement.setString(3, passwordHash);
            statement.setTimestamp(4, Timestamp.from(createdAt));
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                long id = keys.getLong(1);
                return new User(id, username, email, passwordHash, createdAt);
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new UserAlreadyExistsException(username, e);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to save user: " + username, e);
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getTimestamp("created_at").toInstant()
        );
    }
}