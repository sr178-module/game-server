package com.sr178.game.server.msg;

public enum ChannelType{
	//逻辑服管道
	logicChannel,
	//战斗服管道
	battleChannel,
	//api服管道
	apiChannel
	;
	public static ChannelType getByName(String name){
		for(ChannelType type : ChannelType.values()){
             if(name.equals(type.name())){
      	        return type;
            }
         }
		throw new NullPointerException("不存在的枚举值"+name);
	}
	
	public static void main(String[] args) {
		System.out.println(ChannelType.getByName("gateway"));
	}
}
