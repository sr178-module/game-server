package com.sr178.game.server.msg;
/**
 * 服务器类型  发送消息目的地id
 * @author magical
 *
 */
public enum ServerType {
   //战斗服
   BATTLE_SERVER,
   //逻辑服
   LOGIC_SERVER,
   //聊天服
   CHAT_SERVER,
   //网关服
   GATEWAY_SERVER,
   //http api服务器
   API_SERVER;
   
   public static ServerType getByName(String name){
		for(ServerType type : ServerType.values()){
             if(name.equals(type.name())){
      	        return type;
            }
         }
	  throw new NullPointerException("Server不存在的枚举值"+name);
   }
}
