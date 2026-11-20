package io.github.bvilii.yapper.client.backend.service.chat;

import io.github.bvilii.yapper.common.model.MessageInfo;

import java.util.List;

public interface ChatObserver {
    default void onMessageArrived(MessageInfo message) {}

    default void onHistoryLoaded(long roomId, List<MessageInfo> messages) {}
}
