package com.sr178.game.server.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.*;

/**
 * 不提供等待的RingBuffer. 东西放进去之后, 程序之后可以传一个handler进来处理所有已经publish的东西
 * 
 * @author mc
 * 
 * @param <E>
 */
public class RingBufferWrapper<E extends RingBufferEvent> implements
        RingEventSource<E>{

    private static final Logger logger = LoggerFactory
            .getLogger(RingBufferWrapper.class);

    final RingBuffer<E> ringBuffer;
    private final Sequence readSequence;
    private long toRead;

    public RingBufferWrapper(EventFactory<E> factory, int bufferSize,
            boolean isSingleProducer){

        ringBuffer = isSingleProducer ? RingBuffer.createSingleProducer(
                factory, bufferSize, WAIT_STRATEGY) : RingBuffer
                .createMultiProducer(factory, bufferSize, WAIT_STRATEGY);
        readSequence = new Sequence();
        ringBuffer.addGatingSequences(readSequence);
    }

    public boolean hasAvailableCapacity(int cap){
        return ringBuffer.hasAvailableCapacity(cap);
    }

    public long tryNext() throws InsufficientCapacityException{
        return ringBuffer.tryNext();
    }

    public void publish(long sequence){
        ringBuffer.publish(sequence);
    }

    public E get(long sequence){
        return ringBuffer.get(sequence);
    }

    public boolean tryPublishEvent(EventTranslator<E> translator){
        return ringBuffer.tryPublishEvent(translator);
    }

    public <A> boolean tryPublishEvent(EventTranslatorOneArg<E, A> translator,
            A arg0){
        return ringBuffer.tryPublishEvent(translator, arg0);
    }

    public <A, B> boolean tryPublishEvent(
            EventTranslatorTwoArg<E, A, B> translator, A arg0, B arg1){
        return ringBuffer.tryPublishEvent(translator, arg0, arg1);
    }

    public <A, B, C> boolean tryPublishEvent(
            EventTranslatorThreeArg<E, A, B, C> translator, A arg0, B arg1,
            C arg2){
        return ringBuffer.tryPublishEvent(translator, arg0, arg1, arg2);
    }

    public boolean tryPublishEvent(EventTranslatorVararg<E> translator,
            Object... args){
        return ringBuffer.tryPublishEvent(translator, args);
    }

    public boolean hasEventsToHandle(){
        return ringBuffer.isPublished(toRead);
    }

    public void handleEvents(){
        final long lastToRead = toRead;

        long highestToRead = getHighestPublishedSequence(lastToRead);

        if (highestToRead < lastToRead){
            return;
        }

        try{
            E event;
            for (long i = lastToRead; i <= highestToRead; i++){
                try{
                    event = ringBuffer.get(i);
                    try{
                        event.handle();
                    } finally{
                        event.cleanUp();
                    }
                } catch (Throwable ex){
                    logger.error("RingBufferWrapper 处理事件出错", ex);
                }
            }
        } finally{
            toRead = highestToRead + 1;
            readSequence.set(highestToRead);
        }
    }

    long getHighestPublishedSequence(long startSequence){
        for (long i = startSequence;; i++){
            if (!ringBuffer.isPublished(i)){
                return i - 1;
            }
        }
    }

    @Override
    public void handleEvents(RingEventHandler<E> handler){
        final long lastToRead = toRead;

        long highestToRead = getHighestPublishedSequence(lastToRead);

        if (highestToRead < lastToRead){
            return;
        }

        try{
            E event;
            for (long i = lastToRead; i <= highestToRead; i++){
                try{
                    event = ringBuffer.get(i);
                    try{
                        handler.handle(event);
                    } finally{
                        event.cleanUp();
                    }
                } catch (Throwable ex){
                    logger.error("RingBufferWrapper 处理事件出错", ex);
                }
            }
        } finally{
            toRead = highestToRead + 1;
            readSequence.set(highestToRead);
        }
    }

    // ----- wait strategy ------
    private static final NoOpWaitStrategy WAIT_STRATEGY = new NoOpWaitStrategy();

    private static class NoOpWaitStrategy implements WaitStrategy{

        @Override
        public long waitFor(long sequence, Sequence cursor,
                Sequence dependentSequence, SequenceBarrier barrier)
                throws AlertException, InterruptedException, TimeoutException{
            throw new UnsupportedOperationException();
        }

        @Override
        public void signalAllWhenBlocking(){

        }

    }
}
