package io.github.bvilii.yapper.common.message.server;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = LoginOk.class, name = "LOGIN_OK"),
        @JsonSubTypes.Type(value = LoginFailed.class, name = "LOGIN_FAILED"),
        @JsonSubTypes.Type(value = RegisterOk.class, name = "REGISTER_OK"),
        @JsonSubTypes.Type(value = RegisterFailed.class, name = "REGISTER_FAILED"),
        @JsonSubTypes.Type(value = ChatMessage.class, name = "CHAT"),
        @JsonSubTypes.Type(value = ErrorMessage.class, name = "ERROR"),
        @JsonSubTypes.Type(value = RoomCreated.class, name = "ROOM_CREATED"),
        @JsonSubTypes.Type(value = CreateRoomFailed.class, name = "CREATE_ROOM_FAILED")
})
public sealed interface ServerMessage
        permits ChatMessage, ErrorMessage, LoginFailed, LoginOk, RegisterFailed, RegisterOk,
        RoomCreated, CreateRoomFailed {
}