package com.sr178.game.server.channel;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractChannel implements Channel {
	public static final String CHANNEL = "CHANNEL";
	// 用户系列号
	public static final String USER_SEQUENSE = "USER_SEQUENSE";

	// 用户id
	public static final String USER_ID = "USER_ID";
	//用户token
	public static final String TOKEN = "TOKEN";
	//管道加密算法ID
	public static final String SECRET_ID="SECRET_ID";
	//是否为加密的管道
	public static final String IS_VERIFY_DATA = "IS_VERIFY_DATA";
	//服务器类型
	public static final String SERVER_TYPE = "SERVER_TYPE";
	//管道类型
	public static final String CHANNEL_TYPE = "CHANNEL_TYPE";
	//ip
	public static final String IP = "IP";
	
	private Map<String, Object> attrMap = new HashMap<String, Object>();

	private int protocolType;

	private int clientType;
	
	public int getClientType() {
		return clientType;
	}

	public void setClientType(int clientType) {
		this.clientType = clientType;
	}

	public int getProtocolType() {
		return protocolType;
	}

	public void setProtocolType(int protocolType) {
		this.protocolType = protocolType;
	}

	public void addAttribute(String key, Object value) {
		attrMap.put(key, value);
	}

	public Object getAttribute(String key) {
		return attrMap.get(key);
	}

	public void clearAttribute(){
		attrMap.clear();
	}
}
