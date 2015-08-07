package com.sr178.game.server.hold;

public class CountHolder<T> {

    @SuppressWarnings("unchecked")
    public static <T> CountHolder<T>[] newArray(int len){
        return (CountHolder<T>[]) new CountHolder[len];
    }

    private final T key;

    private int value;

    public CountHolder(T key, int value){
        this.key = key;
        this.value = value;
    }

    public T getKey(){
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
