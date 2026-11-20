package io.github.bvilii.yapper.server.persistence;

import java.time.Instant;

public record Message(long id, long roomId, Long senderId, String content, Instant createdAt) {
}