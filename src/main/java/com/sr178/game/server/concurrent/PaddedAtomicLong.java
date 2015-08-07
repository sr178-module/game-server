package com.sr178.game.server.concurrent;

import java.util.concurrent.atomic.AtomicLong;
@SuppressWarnings("serial")
public class PaddedAtomicLong extends AtomicLong{
	private long p1, p2, p3, p4, p5, p6, p7 = 7L;
    /**
     * Default constructor
     */
    public PaddedAtomicLong(){
    }
    /**
     * Construct with an initial value.
     * 
     * @param initialValue
     *            for initialisation
     */
    public PaddedAtomicLong(final long initialValue){
        super(initialValue);
    }

    public long sumPaddingToPreventOptimisation(){
        return p1 + p2 + p3 + p4 + p5 + p6 + p7;
    }
}