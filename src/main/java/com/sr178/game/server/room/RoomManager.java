package com.sr178.game.server.room;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.sr178.game.framework.log.LogSystem;
import com.sr178.game.msgbody.common.model.Msg;
import com.sr178.game.server.msg.ChannelType;

/**
 * 房间管理器
 * @author ws
 *
 */
public class RoomManager{
   private  Map<String,Room> roomMap = new HashMap<String,Room>();
   private static RoomManager roomManager = new RoomManager();
   //房间锁map
   private RoomManager(){}
   
   public static RoomManager getInstatnce(){
	   return roomManager;
   }
   /**
    * 添加房间
    * @param roomId
    * @param roomInfo
    */
   public void addRoom(String roomId,Room roomInfo){
	   roomMap.put(roomId, roomInfo);
   }
   /**
    * 发送消息
    * @param channleType
    * @param notify
    * @param restrictionsRule
    */
   public void sendMsgToRoom(ChannelType channleType,Msg notify,RestrictionsRule restrictionsRule){
	   Room roomInfo = roomMap.get(notify.getMsgHead().getToID());
	   if(roomInfo!=null){
		   roomInfo.sendRoomNotify(channleType, notify, restrictionsRule);
	   }else{
		   LogSystem.warn("不存在的房间id"+notify.getMsgHead().getToID()+",发送消息失败"+new Date());
	   }
   }
}
