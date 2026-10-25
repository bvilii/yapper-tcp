package io.github.bvilii.yapper.server.chat;

public interface Subject {
    void subscribe(Observer observer);
    void unsubscribe(Observer observer);

    void notifyObservers(String msg);
}
