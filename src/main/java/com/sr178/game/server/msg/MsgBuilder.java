package com.sr178.game.server.msg;

import com.sr178.game.msgbody.common.model.ICodeAble;
import com.sr178.game.msgbody.common.model.Msg;
import com.sr178.game.msgbody.common.model.MsgHead;
import com.sr178.game.msgbody.server.NoBody;
import com.sr178.game.server.constant.SystemConstant;

/**
 * 消息建造器
 * 
 * @author mengc
 * 
 */
public class MsgBuilder {
    public static final NoBody EMPTY_BODY = new NoBody();
    public static final Msg ERROR_MSG = new Msg(new MsgHead(SystemConstant.FAIL_CODE),EMPTY_BODY);
    public static final Msg SUCCESS_MSG = new Msg(new MsgHead(SystemConstant.SUCCESS_CODE),EMPTY_BODY);
	private static Msg buildMsg(short fromType, String fromID,
			short toType, String toID, short msgType, int msgSequense, String cmdCode,
			int errorCode,String userSequence,ICodeAble body) {
		MsgHead resHead = new MsgHead(fromType, fromID, toType, toID, msgType,
				msgSequense, cmdCode, errorCode);
		resHead.setUserSequense(userSequence);
		Msg resMsg = new Msg();
		resMsg.setMsgBody(body);
		resMsg.setMsgHead(resHead);
		return resMsg;
	}
	/**
	 * 构建用户请求消息 （用于测试，该消息的响应会直接发送给用户）
	 * @param fromId
	 * @param toServerType
	 * @param cmdCode
	 * @param body
	 * @return
	 */
	public static Msg buildUserRequestMsg(String fromId,ServerType toServerType,String cmdCode,ICodeAble body){
		return buildMsg(MsgHead.TO_OR_FROM_TYPE_USER, fromId,
				MsgHead.TO_OR_FROM_TYPE_SYSTEM, toServerType.name(), MsgHead.TYPEOFREQUEST, 0, cmdCode,
				0,"",body);
	}
	/**
	 * 构建发给用户的下行消息
	 * @param fromServer
	 * @param toUserId
	 * @param cmdCode
	 * @param body
	 * @return
	 */
	public static Msg buildUserMsg(ServerType fromServer,
			String toUserId,String cmdCode,ICodeAble body){
		return buildMsg(MsgHead.TO_OR_FROM_TYPE_SYSTEM, fromServer.name(),
				MsgHead.TO_OR_FROM_TYPE_USER, toUserId, MsgHead.TYPEOFNOTIFY, 0, cmdCode,
				1000,"",body);
	}
	/**
	 * 构建发给用户的房间消息
	 * @param fromServer
	 * @param roomId
	 * @param cmdCode
	 * @param body
	 * @return
	 */
	public static Msg buildUserRoomMsg(ServerType fromServer,
			String roomId,String cmdCode,ICodeAble body){
		return buildMsg(MsgHead.TO_OR_FROM_TYPE_SYSTEM, fromServer.name(),
				MsgHead.TO_OR_FROM_TYPE_ROOM, roomId, MsgHead.TYPEOFNOTIFY, 0, cmdCode,
				1000,"",body);
	}
	/**
	 * 构造服务器间通讯请求消息
	 * @param fromID
	 * @param toID
	 * @param cmdCode
	 * @param body
	 * @return
	 */
	public static Msg buildServerMsg(ServerType fromServer,
			ServerType toServer,String cmdCode,ICodeAble body){
		return buildMsg(MsgHead.TO_OR_FROM_TYPE_SYSTEM, 
				  fromServer.name(), 
			      MsgHead.TO_OR_FROM_TYPE_SYSTEM, 
			      toServer.name(), 
			      MsgHead.TYPEOFREQUEST,0,cmdCode,
			      1000,"",body);
	}
	/**
	 * 服务器代理用户发送的消息 该消息的 响应消息会发送回 源服务器   在逻辑服执行的过程中 会在用户线程中单线程 执行
	 * @param fromServer
	 * @param toServer
	 * @param cmdCode
	 * @param body
	 * @param userId
	 * @return
	 */
	public static Msg buildServerProxUserMsg(ServerType fromServer,
			ServerType toServer,String cmdCode,ICodeAble body,String userId){
		return buildMsg(MsgHead.TO_OR_FROM_TYPE_SYSTEM, 
				  fromServer.name(), 
			      MsgHead.TO_OR_FROM_TYPE_SYSTEM, 
			      toServer.name(), 
			      MsgHead.TYPEOFREQUEST,0,cmdCode,
			      1000,userId,body);
	}
	/**
	 * 根据请求消息构造相应消息
	 * @param request
	 * @param errorCode
	 * @param response
	 * @return
	 */
	public static Msg buildResponseMsg(Msg request,int errorCode,ICodeAble response){
		MsgHead msgHead = request.getMsgHead();
		Msg msgTemp = new Msg();
		msgHead.setMsgType(MsgHead.TYPEOFRESPONSE);
		msgHead.setErrorCode(errorCode);
		msgTemp.setMsgHead(msgHead);
		if(response==null){
			msgTemp.setMsgBody(EMPTY_BODY);
		}else{
		   msgTemp.setMsgBody(response);
		}
		return msgTemp;
	}
}
