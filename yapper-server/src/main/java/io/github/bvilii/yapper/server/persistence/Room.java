package io.github.bvilii.yapper.server.persistence;

import io.github.bvilii.yapper.common.model.RoomType;

import java.time.Instant;

public record Room(long id, String name, RoomType type, Long createdBy, Instant createdAt) {
}
