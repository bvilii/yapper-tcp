package io.github.bvilii.yapper.server.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    Optional<User> findByUsername(String username);

    List<User> findByUsernames(Collection<String> usernames);

    User save(String username, String email, String passwordHash);
}