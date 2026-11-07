package io.github.bvilii.yapper.client.backend.service.auth;

public interface AuthSubject {
    void addObserver(AuthObserver observer);
}