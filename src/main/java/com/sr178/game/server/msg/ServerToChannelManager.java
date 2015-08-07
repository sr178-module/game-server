package com.sr178.game.server.msg;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sr178.game.framework.log.LogSystem;

/**
 * 服务器对应管道管理
 * @author magical
 *
 */
public class ServerToChannelManager {
	private Map<ServerType,ChannelType> serverToChannle = new ConcurrentHashMap<ServerType,ChannelType>();
	private static ServerToChannelManager manager= new ServerToChannelManager();
	private ServerToChannelManager(){}
	public static ServerToChannelManager getInstance(){
		return manager;
	}
	/**
	 * 添加服务器对应的管道
	 * @param serverType
	 * @param channelType
	 */
	public void addServerTypeChannle(ServerType serverType,ChannelType channelType){
		serverToChannle.put(serverType, channelType);
	}
	/**
	 * 根据下发服务器类型获取管道类型
	 * @param serverType
	 * @return
	 */
	public ChannelType getChannel(ServerType serverType){
		ChannelType channelType =  serverToChannle.get(serverType);
		if(channelType==null){
			LogSystem.warn("请确认该"+serverType+"服务器是否启动！ServerToChannelManager.getChannel(serverType),serverType="+serverType+",return channleType = null");
		}
		return channelType;
	}
}
