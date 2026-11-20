package io.github.bvilii.yapper.server.persistence;

public record MessageWithSender(Message message, String senderName) {
}