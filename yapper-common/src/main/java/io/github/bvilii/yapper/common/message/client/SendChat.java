package io.github.bvilii.yapper.common.message.client;

public record SendChat(long roomId, String text) implements ClientMessage {
}