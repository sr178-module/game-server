package com.sr178.game.server.config;


import com.sr178.game.server.msg.ServerType;



public class LocalConfig{
	private ServerType serverType;
	// 本机socket端口号
	private int port;
	//服务器与服务器间通讯md5key
	private String md5Key ;
	//是否开启监控
	private boolean monitor;
	public String getMd5Key() {
		return md5Key;
	}

	public void setMd5Key(String md5Key) {
		this.md5Key = md5Key;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	public ServerType getServerTypeEnum() {
		return serverType;
	}
	public String getServerType() {
		return serverType.name();
	}
	public boolean isMonitor() {
		return monitor;
	}
	public void setMonitor(boolean monitor) {
		this.monitor = monitor;
	}
	public void setServerType(String serverType) {
//		LogSystem.info("serverType="+serverType);
		this.serverType = ServerType.getByName(serverType);
	}
}
