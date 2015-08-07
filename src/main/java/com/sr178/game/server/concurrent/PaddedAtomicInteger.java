package com.sr178.game.server.concurrent;

import java.util.concurrent.atomic.AtomicInteger;
@SuppressWarnings("serial")
public class PaddedAtomicInteger extends AtomicInteger{
	private long p1, p2, p3, p4, p5, p6, p7 = 7L;
    public PaddedAtomicInteger(){
    }
    public PaddedAtomicInteger(int initialValue){
        super(initialValue);
    }
    public long sumPaddingToPreventOptimisation(){
        return p1 + p2 + p3 + p4 + p5 + p6 + p7;
    }
}