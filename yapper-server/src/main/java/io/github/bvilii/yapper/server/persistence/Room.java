package io.github.bvilii.yapper.server.persistence;

import java.time.Instant;

public record Room(long id, String name, Long createdBy, Instant createdAt) {
}