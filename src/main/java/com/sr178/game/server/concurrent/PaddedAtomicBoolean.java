package com.sr178.game.server.concurrent;


import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("serial")
public class PaddedAtomicBoolean extends AtomicBoolean{
    private long p1, p2, p3, p4, p5, p6, p7 = 7L;

    /**
     * Default constructor
     */
    public PaddedAtomicBoolean(){
    }

    /**
     * Construct with an initial value.
     * 
     * @param initialValue
     *            for initialisation
     */
    public PaddedAtomicBoolean(final boolean initialValue){
        super(initialValue);
    }

    public long sumPaddingToPreventOptimisation(){
        return p1 + p2 + p3 + p4 + p5 + p6 + p7;
    }
}