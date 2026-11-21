package io.github.bvilii.yapper.client.backend.service.auth;

public interface AuthObserver {
    default void onLoginSuccess(String username) {}
    default void onLoginFailed(String reason) {}
    default void onRegisterSuccess(String username) {}
    default void onRegisterFailed(String reason) {}
    default void onLoggedOut() {}
}