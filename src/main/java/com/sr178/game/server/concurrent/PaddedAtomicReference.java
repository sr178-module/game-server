package com.sr178.game.server.concurrent;

import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("serial")
public class PaddedAtomicReference<T> extends AtomicReference<T>{
    private long p2, p3, p4, p5, p6, p7;

    public PaddedAtomicReference(T t){
        super(t);
    }

    public long sumPaddingToPreventOptimisation(){
        return p2 + p3 + p4 + p5 + p6 + p7;
    }
}