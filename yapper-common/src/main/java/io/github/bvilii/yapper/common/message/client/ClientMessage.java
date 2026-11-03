package io.github.bvilii.yapper.common.message.client;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = LoginRequest.class, name = "LOGIN"),
        @JsonSubTypes.Type(value = RegisterRequest.class, name = "REGISTER"),
        @JsonSubTypes.Type(value = TokenLoginRequest.class, name = "LOGIN_TOKEN"),
        @JsonSubTypes.Type(value = SendChat.class, name = "CHAT")
})
public sealed interface ClientMessage permits LoginRequest, RegisterRequest, TokenLoginRequest, SendChat {
}