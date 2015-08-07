package com.sr178.game.server.hold;

/**
 * @author Liwei
 *
 */
public class LongLongHolder{

    private final long key;

    private long value;

    public LongLongHolder(long key, long value){
        this.key = key;
        this.value = value;
    }

    public long getKey(){
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
