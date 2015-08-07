package com.sr178.game.server.msg;





import org.springframework.util.StringUtils;

import com.sr178.game.framework.log.LogSystem;
import com.sr178.game.msgbody.common.codec.DataCodecFactory;
import com.sr178.game.msgbody.common.model.ICodeAble;
import com.sr178.game.msgbody.common.model.Msg;
import com.sr178.game.msgbody.common.model.MsgHead;
import com.sr178.game.server.callback.CallBackListenerManager;
import com.sr178.game.server.callback.ICallBackHandler;
import com.sr178.game.server.channel.Channel;
import com.sr178.game.server.config.LocalTools;
import com.sr178.game.server.room.RestrictionsRule;
import com.sr178.game.server.room.RoomManager;

public class MsgDispatchCenter {
   
   /**
    * 加入限制规则发送一条消息
    * @param notify
    * @param restrictionsRule
    */
   private static void disPatchMsg(ChannelType channleType,Msg notify,RestrictionsRule restrictionsRule){
	   if(channleType==null){
		   LogSystem.warn("disPatchMsg的时候channleType为null");
		   return;
	   }
	   MsgHead msgHead = notify.getMsgHead();
	   int toType = msgHead.getToType();
	   String toId = msgHead.getToID();
	   //消息为发给某个用户的
	   if(toType == MsgHead.TO_OR_FROM_TYPE_USER||toType == MsgHead.TO_OR_FROM_TYPE_SYSTEM){
		  if(toId==null||toId.equals("")){
			   throw new NullPointerException("请先设置消息头中的toId值！");
		  }else{
			  ServerMsgManager.getInstance().tryPublishEvent(channleType, notify);;
		  }
	   }else if(toType == MsgHead.TO_OR_FROM_TYPE_ROOM){
			  if(toId==null||toId.equals("")){
				   throw new NullPointerException("请先设置消息头中的toId值！");
			  }
			  RoomManager.getInstatnce().sendMsgToRoom(channleType,notify,restrictionsRule);
	   }
	   else{
		   throw new NullPointerException("设置广播消息的toType类型，即请将toType设置成 MsgHead.TO_USER OR MsgHead.TO_ROOM OR MsgHead.TO_SYSTEM");
	   }
   }
   /**
    * 推消息给用户（网关服务器不能用该接口）
    * @param cmdCode
    * @param roomId
    * @param msgBody
    */
   public static void disPatchUserMsg(String cmdCode,String userId,ICodeAble msgBody){
	   ServerType localServer = LocalTools.getLocalConfig().getServerTypeEnum();
	   if(localServer==ServerType.GATEWAY_SERVER){
		   throw new NullPointerException("网关服务器不能用此接口发送消息");
	   }
	   Msg msg = MsgBuilder.buildUserMsg(localServer,
			   userId,cmdCode,msgBody);
	   disPatchMsg(ServerToChannelManager.getInstance().getChannel(localServer),msg,null);
   }
   /**
    * 给房间发送消息给用户(网关服务器不能调用该接口)
    * @param cmdCode
    * @param roomId
    * @param msgBody
    * @param restrictionsRule
    */
   public static void disPatchUserRoomMsg(String cmdCode,String roomId,ICodeAble msgBody,RestrictionsRule restrictionsRule){
	   ServerType localServer = LocalTools.getLocalConfig().getServerTypeEnum();
	   if(localServer==ServerType.GATEWAY_SERVER){
		   throw new NullPointerException("网关服务器不能用此接口发送消息");
	   }
	   Msg msg = MsgBuilder.buildUserRoomMsg(localServer,
				roomId,cmdCode,msgBody);
	   disPatchMsg(ServerToChannelManager.getInstance().getChannel(localServer),msg,restrictionsRule);
   }
   /**
    * 发送低优先级别的房间广播消息（即繁忙的时候可以被丢弃的消息，如 世界级聊天 ，世界级跑马灯）
    * @param cmdCode
    * @param roomId
    * @param msgBody
    * @param restrictionsRule
    */
   public static void disPatchUserLowerRoomMsg(String cmdCode,String roomId,ICodeAble msgBody,RestrictionsRule restrictionsRule){
	   ServerType localServer = LocalTools.getLocalConfig().getServerTypeEnum();
	   if(localServer==ServerType.GATEWAY_SERVER){
		   throw new NullPointerException("网关服务器不能用此接口发送消息");
	   }
	   Msg msg = MsgBuilder.buildUserRoomMsg(localServer,
				roomId,cmdCode,msgBody);
	   msg.getMsgHead().setPriority(MsgHead.LOWER);
	   disPatchMsg(ServerToChannelManager.getInstance().getChannel(localServer),msg,restrictionsRule);
   }
   /**
    * 发送服务器间的消息
    * 需要回调的
    * @param fromServer
    * @param toServer
    * @param cmdCode
    * @param msgBody
    * @param callBackHandler
    */
   public static void disPatchServerMsg(Channel channel,ServerType toServer,String cmdCode,ICodeAble msgBody,ICallBackHandler callBackHandler,String userId){
	   ServerType fromServer = LocalTools.getLocalConfig().getServerTypeEnum();
	   if(fromServer==toServer){
		   throw new RuntimeException("不允许给自身服务器发送消息！哥！会死循环的~~toServer="+toServer);
	   }
	   Msg msg = null;
	   if(StringUtils.hasText(userId)){
		   msg =  MsgBuilder.buildServerProxUserMsg(fromServer,toServer,cmdCode,msgBody, userId);
	   }else{
		   msg = MsgBuilder.buildServerMsg(fromServer,toServer,cmdCode,msgBody);
	   }
	   if(channel==null){
		   ChannelType channelType = null;
		   //如果网关服务器发送下行服务器消息 则取目的地server的channel 如果非网关服务器 取本地的服务器就行了
		   if(fromServer == ServerType.GATEWAY_SERVER){
			   channelType = ServerToChannelManager.getInstance().getChannel(toServer);
		   }else{
			   channelType = ServerToChannelManager.getInstance().getChannel(fromServer);
		   }
		   if(callBackHandler!=null){
			  int sequence = CallBackListenerManager.getInstance().addListenner(callBackHandler);
			  msg.getMsgHead().setMsgSequense(sequence);
			  disPatchMsg(channelType,msg,null);
		   }else{
			  disPatchMsg(channelType,msg,null);
		   }
	   }else{
		   byte[] datas=null ;
		   if(callBackHandler!=null){
				int sequence = CallBackListenerManager.getInstance().addListenner(callBackHandler);
				msg.getMsgHead().setMsgSequense(sequence);
				datas = DataCodecFactory.getInstance().encodeMsgServer(msg);
				channel.write(datas);
			}else{
				datas = DataCodecFactory.getInstance().encodeMsgServer(msg);
				channel.write(datas);
			  }
	   }
   }
   /**
    * 发送服务器间的消息
    * 不需要执行回调的消息
    * @param toServer
    * @param cmdCode
    * @param msgBody
    */
   public static void disPatchServerMsg(Channel channel,ServerType toServer,String cmdCode,ICodeAble msgBody,ICallBackHandler callBackHandler){
	   disPatchServerMsg(channel,toServer, cmdCode, msgBody,callBackHandler,"");
   }
   /**
    * 发送服务器间的消息
    * 不需要执行回调的消息
    * @param toServer
    * @param cmdCode
    * @param msgBody
    */
   public static void disPatchServerMsg(ServerType toServer,String cmdCode,ICodeAble msgBody){
	   disPatchServerMsg(null,toServer, cmdCode, msgBody,null,"");
   }
   /**
    * 发送服务器间的消息
    * 需要执行回调的消息
    * @param toServer
    * @param cmdCode
    * @param msgBody
    */
   public static void disPatchServerMsg(ServerType toServer,String cmdCode,ICodeAble msgBody,ICallBackHandler callBackHandler){
	   disPatchServerMsg(null,toServer, cmdCode, msgBody,callBackHandler,"");
   }
   
   /**
    * 发送服务器间的消息(代理用户发送消息,该请求在服务器的用户线程中执行)
    * 不需要执行回调的消息
    * @param toServer
    * @param cmdCode
    * @param msgBody
    */
   public static void disPatchServerProxUserMsg(ServerType toServer,String cmdCode,ICodeAble msgBody,ICallBackHandler callBackHandler,String userId){
	   disPatchServerMsg(null,toServer, cmdCode, msgBody,callBackHandler,userId);
   }
   /**
    * 发送服务器间的消息
    * 不需要执行回调的消息 指定发送通道channel
    * @param toServer
    * @param cmdCode
    * @param msgBody
    */
   public static void disPatchServerMsg(Channel channel,ServerType toServer,String cmdCode,ICodeAble msgBody){
	   disPatchServerMsg(channel,toServer, cmdCode, msgBody,null,"");
   }
   /**
    * 发送服务器消息 阻塞等待返回结果的
    * @param toServer
    * @param cmdCode
    * @param msgBody
    * @return
    * @throws TimeoutException 
    */
//   public static Msg disPatchBlockServerMsg(ServerType toServer,String cmdCode,ICodeAble msgBody,long timeOut) throws TimeoutException{
//	   final Msg result = new Msg();
//	   ICallBackHandler callBackHandler =  new ICallBackHandler() {
//			@Override
//			public void onSuccess(Msg msg, Channel channel) {
//				LogSystem.debug("gateway success time="+System.currentTimeMillis());
//				result.setMsgHead(msg.getMsgHead());
//				result.setMsgBody(msg.getMsgBody());
//				result.setData(msg.getData());
//				result.setReceiverTime(msg.getReceiverTime());
//				synchronized (result) {
//					result.notifyAll();
//				}
//			}
//			@Override
//			public void OnFail(Msg msg, Channel channel) {
//				LogSystem.debug("gateway error time="+System.currentTimeMillis());
//				result.setMsgHead(msg.getMsgHead());
//				result.setMsgBody(msg.getMsgBody());
//				result.setData(msg.getData());
//				result.setReceiverTime(msg.getReceiverTime());
//				synchronized (result) {
//					result.notifyAll();
//				}
//			}
//		};
//		MsgDispatchCenter.disPatchServerMsg(toServer, cmdCode, msgBody, callBackHandler);
//		synchronized (result) {
//				try {
//					result.wait(timeOut);
//				} catch (InterruptedException e) {
//					LogSystem.error(e, "");
//				}
//		}
//		if(result.getMsgHead()==null){
//			LogSystem.debug("error,tim="+System.currentTimeMillis());
//			throw new TimeoutException("等待超时！cmdCode = "+cmdCode);
//		}
//	   return result;
//   }
   
   /**
    * 发送服务器消息 阻塞等待返回结果的
    * @param toServer
    * @param cmdCode
    * @param msgBody
    * @return
 * @throws TimeoutException 
 * @throws InterruptedException 
    */
//   public static Msg disPatchBlockServerMsg(ServerType toServer,String cmdCode,ICodeAble msgBody) throws TimeoutException{
//	    return disPatchBlockServerMsg(toServer,cmdCode,msgBody,10000);
//   }
}
