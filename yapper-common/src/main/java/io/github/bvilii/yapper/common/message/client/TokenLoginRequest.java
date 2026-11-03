package io.github.bvilii.yapper.common.message.client;

public record TokenLoginRequest(String token) implements ClientMessage {
}