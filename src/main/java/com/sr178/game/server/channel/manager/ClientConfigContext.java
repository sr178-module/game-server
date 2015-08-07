package com.sr178.game.server.channel.manager;


public class ClientConfigContext extends ConfigContext{
	//管道类型
	protected String channelType;
	protected int nums;
 
	public int getNums() {
		return nums;
	}
	public void setNums(int nums) {
		this.nums = nums;
	}
	public String getChannelType() {
		return channelType;
	}
	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}
	
}
