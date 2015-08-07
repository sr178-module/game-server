package com.sr178.game.server.concurrent;

public interface RingBufferEvent{
    void cleanUp();

    void handle();
}