package io.github.bvilii.yapper.server;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatRoom implements Subject, MessageReceiver {
    private final List<Observer> observers = new CopyOnWriteArrayList<>();

    @Override
    public void onMessageReceived(String msg) {
        notifyObservers(msg);
    }

    @Override
    public void subscribe(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void unsubscribe(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String msg) {
        for(Observer observer : observers){
            observer.update(msg);
        }
    }
}
