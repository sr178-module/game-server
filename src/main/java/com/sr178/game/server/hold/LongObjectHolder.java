package com.sr178.game.server.hold;

/**
 * @author Liwei
 *
 */
public class LongObjectHolder<T> {

    @SuppressWarnings("unchecked")
    public static <T> LongObjectHolder<T>[] newArray(int len){
        return (LongObjectHolder<T>[]) new LongObjectHolder[len];
    }

    private final long key;

    private T value;

    public LongObjectHolder(long key, T value){
        this.key = key;
        this.value = value;
    }

    public long getKey(){
        return key;
    }

    public T getValue(){
        return value;
    }

    public void setValue(T value){
        this.value = value;
    }

}
