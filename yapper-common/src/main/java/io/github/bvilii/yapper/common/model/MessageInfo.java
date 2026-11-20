package io.github.bvilii.yapper.common.model;

public record MessageInfo(long id, long roomId, String sender, String text, long sentAt) {
}
