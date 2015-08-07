package com.sr178.game.server.client.netty;

import java.io.Closeable;
import java.net.InetSocketAddress;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.DefaultChannelPipeline;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.SocketChannelConfig;

import com.sr178.game.framework.log.LogSystem;
import com.sr178.game.msgbody.common.codec.DataCodecFactory;
import com.sr178.game.msgbody.common.model.Msg;
import com.sr178.game.server.bridge.IBridgeEntry;
import com.sr178.game.server.channel.NettyChannel;
import com.sr178.game.server.concurrent.PaddedAtomicBoolean;
import com.sr178.game.server.concurrent.PaddedAtomicReference;
import com.sr178.game.server.socket.netty.codefactory.NettyDecoder;
import com.sr178.game.server.socket.netty.codefactory.NettyEncoder;


/**
 * 断线会自动重连的netty client. 且就算start时, 无法连接, 也会在背后重连
 * @author mc
 *
 */
public class ReconnectNettyClient implements Closeable{

    private final ClientBootstrap clientBootStrap;

    private final ScheduledExecutorService scheduledExec;

    private final PaddedAtomicBoolean closed;

    private final InetSocketAddress remoteAddress;

    private final AtomicReference<Channel> channel;

    private final ClosedNotifier closedNotifier;

    private final PaddedAtomicBoolean isConnected;

    private final PaddedAtomicBoolean isClosedNotified;
    
    
    private IBridgeEntry bridgeEntry;
    
    
    public ReconnectNettyClient(String address, int port, IBridgeEntry bridgeEntry,
            ClosedNotifier closedNotifier,
            ReconnectNettyClientSharedResources res){
        this.scheduledExec = res.getScheduledExec();
        this.closedNotifier = closedNotifier;
        this.bridgeEntry = bridgeEntry;
        this.closed = new PaddedAtomicBoolean(false);
        this.remoteAddress = new InetSocketAddress(address, port);
        this.channel = new PaddedAtomicReference<Channel>(null);
        this.isConnected = new PaddedAtomicBoolean(false);
        if (bridgeEntry != null){
            isClosedNotified = new PaddedAtomicBoolean(false);
        } else{
            isClosedNotified = null;
        }
        
        this.clientBootStrap = new ClientBootstrap(
        		res.getClientSocketChannelFactory());
        
        this.clientBootStrap.setPipelineFactory(new ChannelPipelineFactory(){
			@Override
			public ChannelPipeline getPipeline() throws Exception {
                DefaultChannelPipeline result = new DefaultChannelPipeline();
                result.addLast("frameDecoder",
                        new NettyDecoder(65535, 0, 4, 1, 5));
                result.addLast("frameEncoder", new NettyEncoder());
                result.addLast("handler", new ReconnectHandler());
                return result;
			}
		});
    }

    public ReconnectNettyClient(String address, int port,IBridgeEntry bridgeEntry,
            ReconnectNettyClientSharedResources res){
        this(address, port, bridgeEntry, null, res);
    }

    public ReconnectNettyClient start(){
        if (closed.get()){
            throw new IllegalStateException("要启动个已经关闭的ReconnectNettyClient");
        }
        LogSystem.info("ReconnectNettyClient新建连接: {"+remoteAddress+"}");
        clientBootStrap.connect(remoteAddress);
        return this;
    }
    
    @Override
    public void close(){
        if (!closed.compareAndSet(false, true)){
        	LogSystem.error(new Exception(""), "ReconnectNettyClient已经close了, 又被调用了close: {}");
            return;
        }
        LogSystem.info("ReconnectNettyClient关闭连接: {"+remoteAddress+"}");
        // close channel
        Channel c = channel.getAndSet(DumbChannel.INSTANCE);
        if (c != null){
            c.close();
        } else{
            if (closedNotifier != null){
                if (isClosedNotified.compareAndSet(false, true)){
                    closedNotifier.onClosed();
                }
            }
        }
    }
    public Channel getChannel(){
        return channel.get();
    }
    /**
     * 返回当前channel是否连接着
     * @return
     */
    public boolean isConnected(){
        return isConnected.get();
    }
    /**
     * 是否已关闭. 并不是指channel当前是否关闭 
     * @return
     */
    public boolean isClosed(){
        return closed.get();
    }
    /**
     * 返回true也只表示send的这一刻是连接着的, 还是有一点可能发送失败的. false一定是发送失败
     * @param buffer
     * @return
     */
    public boolean sendMessage(Msg msg){
    	byte[] datas = DataCodecFactory.getInstance().encodeMsgUser(msg);
        Channel c = channel.get();
        if (c != null && c != DumbChannel.INSTANCE){
            c.write(datas);
            return true;
        }
        return false;
    }

    private class ReconnectHandler extends SimpleChannelUpstreamHandler {
    	
    	   @Override
    	   public void messageReceived(
    	            ChannelHandlerContext ctx, MessageEvent e) throws Exception {
    		   NettyChannel channel =  (NettyChannel)ctx.getChannel().getAttachment();
    			try {
    				 bridgeEntry.receivedData(channel, (byte[])e.getMessage());
    			} catch (Exception e1) {
    				LogSystem.error(e1, "");
    			}
    	    }
    	    @Override
    	    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e)
    	            throws Exception {
                Channel c = ctx.getChannel();
                ((SocketChannelConfig) c.getConfig()).setPerformancePreferences(0,
                        1, 2);
    	    }
    	    @Override
            public void channelConnected(ChannelHandlerContext ctx,
                    ChannelStateEvent e) throws Exception{
                LogSystem.debug("ReconnectedNettyClient.channelConnected: {"+remoteAddress+"}");
                Channel c = ctx.getChannel();
                if (!channel.compareAndSet(null, c)){
                	LogSystem.warn(
                            "ReconnectHandler.channelOpen时, cas channel失败. 当前channel: {"+channel.get()+"}");
                    c.close();
                } else{
                    try{
                    	//
                    } finally{
                        isConnected.lazySet(true); // 有些地方依赖与onConnected初始化完成, 才算连接上
                    }
                }
            }
            @Override
            public void channelDisconnected(ChannelHandlerContext ctx,
                    ChannelStateEvent e) throws Exception{
                LogSystem.debug("ReconnectedNettyClient.channelDisconnected: {"+remoteAddress+"}");
                isConnected.lazySet(false);
                try{
                    if (!channel.compareAndSet(ctx.getChannel(), null)){
                        if (!closed.get()){
                            LogSystem.error(new Exception(),
                                    "ReconnectHandler.channelDisconnected时, channel cas到null失败, 且当前并未closed. 当前channel: {"+channel.get()+"}");
                        } else{
                            // 连接关闭了, 也调用一次onDisconnect
//                            bridgeEntry.onDisconnect();
                        }
                    } else{
//                        bridgeEntry.onDisconnect();
                    }
                } finally{
                    isConnected.lazySet(false);
                }
                super.channelDisconnected(ctx, e);
            }

            @Override
            public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
                    throws Exception{
                LogSystem.debug("ReconnectedNettyClient.channelClosed: {"+remoteAddress+"}");
                super.channelClosed(ctx, e);
                // 如果没有关闭, 则重连
                if (!closed.get()){
                    scheduledExec.schedule(new Runnable(){
                        @Override
                        public void run(){
                            clientBootStrap.connect(remoteAddress);
                        }
                    }, 1, TimeUnit.SECONDS);
                } else{
                    if (closedNotifier != null){
                        if (isClosedNotified.compareAndSet(false, true)){
                            closedNotifier.onClosed();
                        }
                    }
                }
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e){
                if (e.getCause() instanceof java.io.IOException){
                } else{
                    e.getCause().printStackTrace();
                }

                e.getChannel().close();
            }
    }

    /**
     * 在整个ReconnectNettyClient关闭时, 会被调用. 连接暂时性断开不会被调用
     * @author mc
     *
     */
    public interface ClosedNotifier{
        void onClosed();
    }

}
