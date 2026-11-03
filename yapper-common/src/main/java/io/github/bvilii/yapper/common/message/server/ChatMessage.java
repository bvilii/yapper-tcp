package io.github.bvilii.yapper.common.message.server;

public record ChatMessage(long roomId, String sender, String text, long sentAt) implements ServerMessage {
}