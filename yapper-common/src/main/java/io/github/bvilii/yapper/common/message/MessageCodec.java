package io.github.bvilii.yapper.common.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bvilii.yapper.common.message.client.ClientMessage;
import io.github.bvilii.yapper.common.message.server.ServerMessage;

public class MessageCodec {
    private final ObjectMapper mapper = new ObjectMapper();

    public String encode(ClientMessage message) {
        return write(message);
    }

    public String encode(ServerMessage message) {
        return write(message);
    }

    public ClientMessage decodeClientMessage(String json) throws InvalidMessageException {
        return read(json, ClientMessage.class);
    }

    public ServerMessage decodeServerMessage(String json) throws InvalidMessageException {
        return read(json, ServerMessage.class);
    }

    private String write(Object message) {
        try {
            return mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not encode message: " + message, e);
        }
    }

    private <T> T read(String json, Class<T> type) throws InvalidMessageException {
        try {
            return mapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new InvalidMessageException("Invalid message: " + json, e);
        }
    }
}