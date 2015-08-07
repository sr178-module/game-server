package com.sr178.game.server.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.InsufficientCapacityException;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.sr178.game.framework.log.LogSystem;
import com.sr178.game.server.hold.ObjectHolder;

/**
 * 单例线程池
 * 而不用新建Runnable来使此线程处理消息
 *
 * @author mc
 *
 */
public class DisruptorExecutor implements Executor{

    private final int id;
    private final Disruptor<DisruptorEvent> disruptor;
    private final RingBuffer<DisruptorEvent> ringBuffer;
    private final ExecutorService exec;

    private final Thread thread;

    /**
     * 单线程处理消息或任务
     *
     * @param bufferSize
     *            可缓存的待处理任务个数, 必须为2的倍数. 待处理任务个数超过此上限时, 提交任务的线程将会等待
     */
    public DisruptorExecutor(int id, int bufferSize, final String name){
        if (!isPowerOf2(bufferSize)){
            throw new IllegalArgumentException("bufferSize 必须为2的倍数");
        }
        this.id = id;
        exec = Executors.newSingleThreadExecutor(new ThreadFactory(){

            @Override
            public Thread newThread(Runnable r){
                Thread t = new Thread(r, name);
                t.setPriority(Thread.MAX_PRIORITY);
                return t;
            }

        });

        final ObjectHolder<Thread> ref = new ObjectHolder<>(null);
        final CountDownLatch latch = new CountDownLatch(1);
        exec.execute(new Runnable(){
            @Override
            public void run(){
                ref.set(Thread.currentThread());
                latch.countDown();
            }
        });
        try{
            latch.await();
        } catch (InterruptedException e){
            throw new RuntimeException(e); // should not be interrupted
        }
        thread = ref.get();
        if (thread == null){
            throw new RuntimeException("Thread = null");
        }

        disruptor = new Disruptor<DisruptorEvent>(DisruptorEvent.EVENT_FACTORY,
                bufferSize, exec, ProducerType.MULTI,
                new BlockingWaitStrategy());

        DisruptorEventHandler[] handlers = new DisruptorEventHandler[1];

        handlers[0] = new DisruptorEventHandler();

        disruptor.handleEventsWith(handlers);

        ringBuffer = disruptor.start();
    }

    public int getId(){
        return id;
    }

    public Thread getThread(){
        return thread;
    }

    public int getAvailableCapacity(){
        return (int) ringBuffer.remainingCapacity();
    }

    private static boolean isPowerOf2(int v){
        return v > 0 && (v & (v - 1)) == 0;
    }

    @Override
    public void execute(Runnable command){
        long sequence = ringBuffer.next();
        try{
            DisruptorEvent event = ringBuffer.get(sequence);
            event.runnable = command;
        } finally{
            ringBuffer.publish(sequence);
        }
    }

    /**
     * 如果buffer还有空位, 就处理, 没有就返回false
     *
     * @param command
     * @return
     */
    public boolean tryExecute(Runnable command){
        try{
            long sequence = ringBuffer.tryNext();
            try{
                DisruptorEvent event = ringBuffer.get(sequence);
                event.runnable = command;
            } finally{
                ringBuffer.publish(sequence);
            }
            return true;
        } catch (InsufficientCapacityException e){
            return false;
        }
    }

    /**
     * 关闭这个处理器, 之前提交的任务都会被执行. 调用此方法后, 须确保不会再提交新任务, 不然可能导致此方法无法完成
     */
    public void shutdown(){
        disruptor.shutdown();
        exec.shutdown();
        try{
            exec.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    private static class DisruptorEventHandler implements
            EventHandler<DisruptorEvent>, WorkHandler<DisruptorEvent>{

        @Override
        public void onEvent(DisruptorEvent event, long sequence,
                boolean endOfBatch) throws Exception{
            event.process();
        }

        @Override
        public void onEvent(DisruptorEvent event) throws Exception{
            event.process();
        }

    }

    private static class DisruptorEvent{
        private static final EventFactory<DisruptorEvent> EVENT_FACTORY = new EventFactory<DisruptorEvent>(){
            @Override
            public DisruptorEvent newInstance(){
                return new DisruptorEvent();
            }
        };
        private Runnable runnable;
        private void process(){
            if (runnable != null){
                try{
                    runnable.run();
                } catch (Exception ex){
                    LogSystem.error(ex,"DisruptorExecutor 处理Runnable出错");
                } finally{
                    runnable = null;
                }
            } else{
            	LogSystem.warn("runnable == null");
            }
        }

    }

}
