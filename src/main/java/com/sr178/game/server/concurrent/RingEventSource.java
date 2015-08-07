package com.sr178.game.server.concurrent;

public interface RingEventSource<E> {
    void handleEvents(RingEventHandler<E> handler);
}
