package com.sr178.game.server.msg;

import java.util.ArrayList;
import java.util.List;

import com.sr178.game.msgbody.common.model.Msg;
/**
 * 服务器消息队列
 * @author magical
 *
 */
public class ServerMsgManager {
  private static ServerMsgManager serverMsgManager = new ServerMsgManager();
  private ServerMsgManager(){}
//  private RingBufferWrapper<SendServerMsgEvent> serverMsgRingBufferWrapper = new RingBufferWrapper<>(SendServerMsgEvent.SendServerMsgEventFactory, 1024*8, false);
  public static ServerMsgManager getInstance(){
	  return serverMsgManager;
  }
   public void tryPublishEvent(ChannelType channelType,Msg msg){
		  List<Msg> msgs = new ArrayList<Msg>(1);
		  msgs.add(msg);
		  //不再延时发送  直接发送出去
//		  ServerChannelManager.getInstance().sendMsgToChannel(channelType, msgs);
		  tryPublishEvent(channelType,msgs);
   }
/**
  * 发布 发送消息job
  * @param sessionList
*/
	public void tryPublishEvent(ChannelType channelType,List<Msg> msgs){
//		try {
//			long sequence = serverMsgRingBufferWrapper.tryNext();
//			try{
//				SendServerMsgEvent event = serverMsgRingBufferWrapper.get(sequence);
//				event.setMsgs(msgs);
//				event.setChannelType(channelType);
//			}finally{
//				serverMsgRingBufferWrapper.publish(sequence);
//			}
//		} catch (InsufficientCapacityException e) {
//			LogSystem.error(e, "发送服务器信息的buffer池子满了！！~~~~~~");
//		}
		//不再延时发送  直接发送出去
		ServerChannelManager.getInstance().sendMsgToChannel(channelType, msgs);
	}
//	
//	public void handleEvent(){
//		serverMsgRingBufferWrapper.handleEvents();
//	}
}

//class SendServerMsgEvent implements RingBufferEvent{
//	private ChannelType channelType;
//	private List<Msg> msgs;
//
//	public ChannelType getChannelType() {
//		return channelType;
//	}
//
//	public void setChannelType(ChannelType channelType) {
//		this.channelType = channelType;
//	}
//
//	public List<Msg> getMsgs() {
//		return msgs;
//	}
//
//	public void setMsgs(List<Msg> msgs) {
//		this.msgs = msgs;
//	}
//
//	public static final EventFactory<SendServerMsgEvent> SendServerMsgEventFactory = new EventFactory<SendServerMsgEvent>() {
//		@Override
//		public SendServerMsgEvent newInstance() {
//			return new SendServerMsgEvent();
//		}
//	}; 
//	@Override
//	public void cleanUp() {
//         		
//	}
//	@Override
//	public void handle() {
//			ServerChannelManager.getInstance().sendMsgToChannel(channelType, msgs);
//	}
//}