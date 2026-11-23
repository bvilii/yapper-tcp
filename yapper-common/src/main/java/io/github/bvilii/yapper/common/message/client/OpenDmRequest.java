package io.github.bvilii.yapper.common.message.client;

/** Open the direct chat with this user, creating it if it does not exist yet. */
public record OpenDmRequest(String username) implements ClientMessage {
}
