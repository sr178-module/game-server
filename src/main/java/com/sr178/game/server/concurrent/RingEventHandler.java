package com.sr178.game.server.concurrent;

public interface RingEventHandler<E> {
    void handle(E event);
}