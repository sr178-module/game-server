package com.sr178.game.server.hold;

/**
 * @author Liwei
 *
 */
public class IntLongHolder{

    private final int key;

    private long value;

    public IntLongHolder(int key, long value){
        this.key = key;
        this.value = value;
    }

    public int getKey(){
        return key;
    }

    public long getValue(){
        return value;
    }

    public void setValue(long value){
        this.value = value;
    }

    public final long incrementAndGet(){
        return ++value;
    }

    public final long decrementAndGet(){
        return --value;
    }

    public void add(long toAdd){
        value += toAdd;
    }
}
