package com.sr178.game.server.hold;

public class LongHolder{

    private long value;

    public LongHolder(long value){
        super();
        this.value = value;
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
}
