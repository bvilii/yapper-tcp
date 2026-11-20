package io.github.bvilii.yapper.common.message.server;

import io.github.bvilii.yapper.common.model.MessageInfo;

public record ChatMessage(MessageInfo message) implements ServerMessage {
}