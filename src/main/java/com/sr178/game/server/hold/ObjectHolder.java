package com.sr178.game.server.hold;

public class ObjectHolder<T> {

    private T value;

    public ObjectHolder(T initialValue){
        this.value = initialValue;
    }

    public T get(){
        return value;
    }

    public void set(T newValue){
        this.value = newValue;
    }

    @Override
    public String toString(){
        return value.toString();
    }
}
