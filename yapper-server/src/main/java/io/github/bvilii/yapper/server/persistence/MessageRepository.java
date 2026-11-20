package io.github.bvilii.yapper.server.persistence;

import java.util.List;

public interface MessageRepository {

    Message save(long roomId, long senderId, String content);

    List<MessageWithSender> findLatestByRoom(long roomId, int limit);
}