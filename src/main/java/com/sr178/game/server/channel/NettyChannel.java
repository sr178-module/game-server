package com.sr178.game.server.channel;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFutureListener;

import com.sr178.game.server.util.SequenseManager;

public class NettyChannel extends AbstractChannel {
	private final Channel channel;
	private String channelID;
	public NettyChannel(Channel channel) {
		channelID = SequenseManager.getInstance().generateStaticseq()+"";
		this.channel = channel;
	}
	@Override
	public void write(byte[] datas) {
		channel.write(datas);
	}
	@Override
	public void writeAfterClose(byte[] datas) {
		channel.write(datas).addListener(ChannelFutureListener.CLOSE);
	}
	@Override
	public boolean isClosed() {
		return !channel.isConnected()||!channel.isOpen();
	}
	@Override
	public void close() {
		channel.close();
	}
	@Override
	public String getChannelId() {
		return channelID;
	}
	@Override
	public boolean isWriteAble() {
		return channel.isWritable();
	}
}
