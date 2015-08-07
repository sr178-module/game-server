package com.sr178.game.server.msg;

import com.sr178.game.server.channel.Channel;

public class MiddlewareMsg {
	private Channel channel;
	private byte[] b;
	private long receverTime;

	public long getReceverTime() {
		return receverTime;
	}

	public void setReceverTime(long receverTime) {
		this.receverTime = receverTime;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public byte[] getB() {
		return b;
	}

	public void setB(byte[] b) {
		this.b = b;
	}

	public MiddlewareMsg(Channel channel, byte[] b,long receverTime) {
		super();
		this.channel = channel;
		this.b = b;
		this.receverTime = receverTime;
	}
}
