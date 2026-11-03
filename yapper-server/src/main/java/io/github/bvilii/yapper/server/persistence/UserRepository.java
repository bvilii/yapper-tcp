package io.github.bvilii.yapper.server.persistence;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findByUsername(String username);

    User save(String username, String email, String passwordHash);
}