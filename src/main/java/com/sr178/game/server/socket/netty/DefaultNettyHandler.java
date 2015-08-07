package com.sr178.game.server.socket.netty;


import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.sr178.game.framework.log.LogSystem;
import com.sr178.game.server.bridge.IBridgeEntry;
import com.sr178.game.server.channel.Channel;
import com.sr178.game.server.channel.NettyChannel;

public class DefaultNettyHandler extends SimpleChannelUpstreamHandler {
	
	private IBridgeEntry bridgeEntry;
	   @Override
	   public void messageReceived(
	            ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		   Channel channel =  (Channel)ctx.getChannel().getAttachment();
			try {
				 bridgeEntry.receivedData(channel, (byte[])e.getMessage());
			} catch (Exception e1) {
				LogSystem.error(e1, "");
			}
	    }
	    @Override
	    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e)
	            throws Exception {
	    }
	    
	    @Override
	    public void channelConnected(
	            ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
	    	NettyChannel channel = new NettyChannel(ctx.getChannel());
			ctx.getChannel().setAttachment(channel);
	    }
	    @Override
	    public void channelDisconnected(
	            ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
	        super.channelDisconnected(ctx, e);
	    }
		public IBridgeEntry getBridgeEntry() {
			return bridgeEntry;
		}
		public void setBridgeEntry(IBridgeEntry bridgeEntry) {
			this.bridgeEntry = bridgeEntry;
		}
}
