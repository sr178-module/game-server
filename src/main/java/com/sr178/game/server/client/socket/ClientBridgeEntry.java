package com.sr178.game.server.client.socket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sr178.game.framework.log.LogSystem;
import com.sr178.game.msgbody.common.codec.DataCodecFactory;
import com.sr178.game.msgbody.common.model.Msg;
import com.sr178.game.server.bridge.IBridgeEntry;
import com.sr178.game.server.callback.ICallBackHandler;
import com.sr178.game.server.channel.Channel;
import com.sr178.game.server.constant.SystemConstant;

public class ClientBridgeEntry implements IBridgeEntry {

	private Map<Long,ICallBackHandler> handlers = new ConcurrentHashMap<Long,ICallBackHandler>();
	private MsgCodecType msgCodecType;
	
	public ClientBridgeEntry(MsgCodecType msgCodecType){
		this.msgCodecType = msgCodecType;
	}
	
	public void addCallBackHandler(long key,ICallBackHandler callBackHandler){
		handlers.put(key, callBackHandler);
	}
	/**
	 * 连接关闭时 清除这些handler
	 * @param key
	 */
	public void removeCallBackHandler(long key){
		ICallBackHandler backHandler = handlers.remove(key);
		if(backHandler!=null){
			backHandler.OnFail(null, null);
		}
	}
	@Override
	public void receivedData(Channel channel, byte[] datas) throws Exception {
		List<Msg> msgs = null;
		 if(msgCodecType == MsgCodecType.Server){
			  msgs = DataCodecFactory.getInstance().decodeMsgServer(datas);
		 }else{
			 msgs = new ArrayList<Msg>();
			 msgs.addAll(DataCodecFactory.getInstance().decodeMsgUser(datas));
		 }
		 for(Msg msg:msgs){
				 ICallBackHandler backHandler = handlers.get(channel.getChannelId());
				 if(backHandler!=null){
					 if(msg.getMsgHead().getErrorCode()!=SystemConstant.SUCCESS_CODE){
						 backHandler.OnFail(msg, channel);
					 }else{
						 backHandler.onSuccess(msg, channel);
					 }
				 }else{
					 LogSystem.warn("CallbackHandler Is null!But has a response msg"+msg.getMsgHead().toString());
				 }
		 }
	}

}
