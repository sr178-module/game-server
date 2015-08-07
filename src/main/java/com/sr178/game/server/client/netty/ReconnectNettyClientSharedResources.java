package com.sr178.game.server.client.netty;

import java.io.Closeable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.jboss.netty.channel.socket.ClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import com.sr178.game.server.threadpool.MyThreadFactory;




public class ReconnectNettyClientSharedResources implements Closeable{

    private final ClientSocketChannelFactory channelFactory;

    private final ExecutorService exec;

    private final ScheduledExecutorService scheduledExec;
    

    public ReconnectNettyClientSharedResources(){
        this.exec = Executors.newCachedThreadPool();
        this.scheduledExec = Executors.newScheduledThreadPool(1,
                new MyThreadFactory("NETTY_SHARED_RECONNECT_SCHEDULED"));
        this.channelFactory = new NioClientSocketChannelFactory(exec, exec);
    }

    public ClientSocketChannelFactory getClientSocketChannelFactory(){
        return channelFactory;
    }


	public ScheduledExecutorService getScheduledExec(){
        return scheduledExec;
    }

    @Override
    public void close(){
        scheduledExec.shutdown();
        channelFactory.shutdown();
        exec.shutdown();
    }
}
