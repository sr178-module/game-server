package com.sr178.game.server.socket.mina.heartbeat;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.keepalive.KeepAliveFilter;

public class HachiKeepAliveFilter extends KeepAliveFilter {

	private static final int INTERVAL = 60;// in seconds
	private static final int TIMEOUT = 20; // in seconds

	public HachiKeepAliveFilter() {
		super(new KeepAliveMessageFactoryImpl(), IdleStatus.BOTH_IDLE,
				new KeepAliveRequestTimeoutHandlerImpl(), INTERVAL, TIMEOUT);
		this.setForwardEvent(false); // 此消息不会继续传递，不会被业务层看见
	}

}
