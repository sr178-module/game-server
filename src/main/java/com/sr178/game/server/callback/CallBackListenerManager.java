package com.sr178.game.server.callback;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

import com.sr178.game.framework.log.LogSystem;
import com.sr178.game.msgbody.common.model.Msg;
import com.sr178.game.server.channel.Channel;
import com.sr178.game.server.concurrent.PaddedAtomicLong;
import com.sr178.game.server.constant.SystemConstant;
import com.sr178.game.server.msg.MsgBuilder;

public class CallBackListenerManager {
   private Map<Integer,CallBackBean> callBackMap = new ConcurrentHashMap<Integer,CallBackBean>(2048);
   private static CallBackListenerManager listenerManager = new CallBackListenerManager();
   private PaddedAtomicLong callBackSequence = new PaddedAtomicLong(0);
   //过期时间3秒
   private final static long TIME_OUT = 10000;
   //默认超时时间
   private CallBackListenerManager(){}
   
   public static CallBackListenerManager getInstance(){
	   return listenerManager;
   }
   private int getSequence(){
	  long nowLongSequence =  callBackSequence.getAndIncrement();
	  if(nowLongSequence>Integer.MAX_VALUE){
		  return (int)(nowLongSequence%Integer.MAX_VALUE);
	  }else{
		  return (int)nowLongSequence;
	  }
   }
   /**
    * 添加监听
    * @param requestMsg
    * @param callBackHandler
    */
   public int addListenner(ICallBackHandler callBackHandler){
	   int sequence = getSequence();
	   CallBackBean callBackBean = new CallBackBean(callBackHandler, System.currentTimeMillis());
	   callBackMap.put(sequence, callBackBean);
	   checkTimeOutHandler();
	   return sequence;
   }
   
   public void checkTimeOutHandler(){
	   for(Entry<Integer, CallBackBean> entry:callBackMap.entrySet()){
		   Integer key = entry.getKey();
		   CallBackBean callBackBean = entry.getValue();
		   if(System.currentTimeMillis()-callBackBean.getAddTime()>TIME_OUT){
			   callBackMap.remove(key).getCallBackHandler().OnFail(MsgBuilder.ERROR_MSG, null);
			   LogSystem.error(new TimeoutException(), "监听超时，消息sequence="+key+",监听添加时间"+callBackBean.getAddTime()+",now tome"+System.currentTimeMillis());
		   }
	   }
   }
   /**
    * 执行callback响应逻辑
    * @param responseMsg
    * @param channel
    * @param msgSequence
    */
   public void executeCallBack(Msg responseMsg,Channel channel){
	   LogSystem.debug("收到响应消息,cmdCode="+responseMsg.getMsgHead().getCmdCode()+",errorCode="+responseMsg.getMsgHead().getErrorCode()+",time="+System.currentTimeMillis());
	   CallBackBean callBackBean = callBackMap.remove(responseMsg.getMsgHead().getMsgSequense());
	   if(callBackBean!=null){
		   if(responseMsg.getMsgHead().getErrorCode()!=SystemConstant.SUCCESS_CODE){
			   callBackBean.getCallBackHandler().OnFail(responseMsg, channel);
		   }else{
			   callBackBean.getCallBackHandler().onSuccess(responseMsg, channel);
		   }
	   }else{
		   throw new NullPointerException("响应来了，但找不到监听 cmdCode="+responseMsg.getMsgHead().getCmdCode()+",sequence = "+responseMsg.getMsgHead().getMsgSequense());
	   }
   }
}

class CallBackBean{
	private ICallBackHandler callBackHandler;
	private long addTime;
	public ICallBackHandler getCallBackHandler() {
		return callBackHandler;
	}
	public void setCallBackHandler(ICallBackHandler callBackHandler) {
		this.callBackHandler = callBackHandler;
	}
	public long getAddTime() {
		return addTime;
	}
	public void setAddTime(long addTime) {
		this.addTime = addTime;
	}
	public CallBackBean(ICallBackHandler callBackHandler, long addTime) {
		super();
		this.callBackHandler = callBackHandler;
		this.addTime = addTime;
	}
}
