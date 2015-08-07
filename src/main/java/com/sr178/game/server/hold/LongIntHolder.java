package com.sr178.game.server.hold;

/**
 * @author Liwei
 *
 */
public class LongIntHolder{

    private final long key;

    private int value;

    public LongIntHolder(long key, int value){
        this.key = key;
        this.value = value;
    }

    public long getKey(){
        return key;
    }

    public int getValue(){
        return value;
    }

    public void setValue(int value){
        this.value = value;
    }

    public final int incrementAndGet(){
        return ++value;
    }

    public final int decrementAndGet(){
        return --value;
    }
}
