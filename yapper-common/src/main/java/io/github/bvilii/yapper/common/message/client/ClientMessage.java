package io.github.bvilii.yapper.common.message.client;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = LoginRequest.class, name = "LOGIN"),
        @JsonSubTypes.Type(value = RegisterRequest.class, name = "REGISTER"),
        @JsonSubTypes.Type(value = SendChat.class, name = "CHAT"),
        @JsonSubTypes.Type(value = CreateRoomRequest.class, name = "CREATE_ROOM"),
        @JsonSubTypes.Type(value = RoomListRequest.class, name = "ROOM_LIST_REQUEST"),
        @JsonSubTypes.Type(value = RoomHistoryRequest.class, name = "ROOM_HISTORY_REQUEST"),
        @JsonSubTypes.Type(value = Logout.class, name = "LOGOUT"),
        @JsonSubTypes.Type(value = OpenDmRequest.class, name = "OPEN_DM"),
        @JsonSubTypes.Type(value = DeleteRoomRequest.class, name = "DELETE_ROOM_REQUEST")

})
public sealed interface ClientMessage
        permits CreateRoomRequest, DeleteRoomRequest, LoginRequest, Logout, OpenDmRequest, RegisterRequest, RoomHistoryRequest, RoomListRequest, SendChat {
}