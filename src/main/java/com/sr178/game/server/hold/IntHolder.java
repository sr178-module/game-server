package com.sr178.game.server.hold;

public class IntHolder{

    private int value;

    public IntHolder(int value){
        super();
        this.value = value;
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
}
