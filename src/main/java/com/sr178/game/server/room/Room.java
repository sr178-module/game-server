package com.sr178.game.server.room;



import java.util.List;

import com.google.common.collect.Lists;
import com.sr178.game.framework.log.LogSystem;
import com.sr178.game.msgbody.common.model.Msg;
import com.sr178.game.msgbody.common.model.MsgHead;
import com.sr178.game.server.msg.ChannelType;
import com.sr178.game.server.msg.ServerMsgManager;

/**
 * 房間bean
 * @author ws
 *
 */

public abstract class Room {
   /**
    * 发送房间广播
    * @param msg
    */
   protected void sendRoomNotify(ChannelType channleType,Msg msg,RestrictionsRule restrictionsRule){
	   if(msg==null) return;
	    List<String> users = getAllUser();
  		LogSystem.debug("广播消息cmdCode:"+msg.getMsgHead().getCmdCode()+"，房间总人数"+users.size());
  		List<Msg> notifyList = Lists.newArrayList();
		for (String userId : users) {
			if (restrictionsRule != null) {
				if (restrictionsRule.isRestrictionUser(userId)) {
					continue;
				}
			}
			Msg notify = new Msg();
			MsgHead notifyHead = msg.getMsgHead().clone();
			notifyHead.setToType(MsgHead.TO_OR_FROM_TYPE_USER);
			notifyHead.setToID(userId);
			notifyHead.setMsgType(MsgHead.TYPEOFNOTIFY);
			notify.setMsgHead(notifyHead);
			notify.setMsgBody(msg.getMsgBody());
			notifyList.add(notify);
		}
		ServerMsgManager.getInstance().tryPublishEvent(channleType, notifyList);
   }
   
   public abstract List<String> getAllUser();
}
