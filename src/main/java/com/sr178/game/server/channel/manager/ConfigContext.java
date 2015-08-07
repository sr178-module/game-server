package com.sr178.game.server.channel.manager;

import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.sr178.game.server.threadpool.ThreadPoolBean;

public class ConfigContext {
	private String connectorId;
	/**
	 *  监控IP
	 */
	private String ip = "127.0.0.1";

	/**
	 *  监控端口
	 */
	private int port = 10001;

	/***
	 * 逻辑处理器
	 */
	private SimpleChannelUpstreamHandler handler;
	/**
	 * 处理的线程池
	 */
	private ThreadPoolBean pool;
	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public ThreadPoolBean getPool() {
		return pool;
	}

	public void setPool(ThreadPoolBean pool) {
		this.pool = pool;
	}

	public String getConnectorId() {
		return connectorId;
	}

	public void setConnectorId(String connectorId) {
		this.connectorId = connectorId;
	}

	public SimpleChannelUpstreamHandler getHandler() {
		return handler;
	}

	public void setHandler(SimpleChannelUpstreamHandler handler) {
		this.handler = handler;
	}
	
}
