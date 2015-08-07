package com.sr178.game.server.hold;

public class BooleanHolder{

    private boolean value;

    public BooleanHolder(boolean initialValue){
        this.value = initialValue;
    }

    public boolean get(){
        return value;
    }

    public void set(boolean newValue){
        this.value = newValue;
    }
}
