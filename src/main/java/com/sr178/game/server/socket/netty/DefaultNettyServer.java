package com.sr178.game.server.socket.netty;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.DefaultChannelPipeline;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.sr178.game.framework.log.LogSystem;
import com.sr178.game.server.manager.IServer;
import com.sr178.game.server.socket.netty.codefactory.NettyDecoder;
import com.sr178.game.server.socket.netty.codefactory.NettyEncoder;

public class DefaultNettyServer implements IServer{
    private ChannelFactory factory;
    private ServerBootstrap serverBootstrap;
    private ChannelGroup allChannels;
    private int port;
    private SimpleChannelUpstreamHandler handler;
	@Override
	public void start() {
	        allChannels = new DefaultChannelGroup();
	        factory = new NioServerSocketChannelFactory(
	                Executors.newCachedThreadPool(),
	                Executors.newCachedThreadPool());
	        serverBootstrap = new ServerBootstrap(factory);
	        serverBootstrap.setOption("child.tcpNoDelay", true);
	        serverBootstrap.setOption("child.keepAlive", false);
	        serverBootstrap.setOption("child.sendBufferSize", 1024 * 4);
	        serverBootstrap.setOption("child.receiveBufferSize", 1024 * 2);
	        serverBootstrap.setPipelineFactory(new ChannelPipelineFactory(){
	            @Override
	            public ChannelPipeline getPipeline() throws Exception{
	                DefaultChannelPipeline result = new DefaultChannelPipeline();
	                result.addLast("frameDecoder",
	                        new NettyDecoder(65535, 0, 4, 1, 5));
	                result.addLast("frameEncoder", new NettyEncoder());
	                result.addLast("handler",handler);
	                return result;
	            }
	        });
	        serverBootstrap.bind(new InetSocketAddress(
	                port));
	        LogSystem.info("netty服务器启动成功，监听端口"+port);
	}

	@Override
	public void restart() {
		shutdown();
		start();
	}
	
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public SimpleChannelUpstreamHandler getHandler() {
		return handler;
	}

	public void setHandler(SimpleChannelUpstreamHandler handler) {
		this.handler = handler;
	}

	@Override
	public void shutdown() {
		allChannels.close().awaitUninterruptibly();
		serverBootstrap.releaseExternalResources();
	}

}
