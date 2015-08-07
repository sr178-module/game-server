package com.sr178.game.server.client.socket;






import java.util.List;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.sr178.game.framework.log.LogSystem;
import com.sr178.game.msgbody.common.codec.DataCodecFactory;
import com.sr178.game.msgbody.common.model.Msg;
import com.sr178.game.server.callback.ICallBackHandler;
import com.sr178.game.server.channel.AbstractChannel;
import com.sr178.game.server.channel.MinaChannel;
import com.sr178.game.server.constant.SystemConstant;

public class MinaClientHandler extends IoHandlerAdapter {
	public static final String HANDLER = "HANDLER";
	public static final String ShortResult = "ShortResult";
    private MsgCodecType codecType;	
    
    public MinaClientHandler(MsgCodecType codecType){
    	this.codecType = codecType ;
    }
	@Override
	public void exceptionCaught(IoSession ioSession, Throwable arg1)
			throws Exception {
		// TODO Auto-generated method stub
		ioSession.close(true);
	}
	@Override
	public void messageReceived(IoSession ioSession, Object arg1) throws Exception {
		// TODO Auto-generated method stub
		byte[] bytes = (byte[])arg1;
		Object o = ioSession.getAttribute(AbstractChannel.CHANNEL);
		MinaChannel channel  = (MinaChannel)o;
		List<Msg> msgs = null;
		 if(codecType == MsgCodecType.Server){
			 msgs = DataCodecFactory.getInstance().decodeMsgServer(bytes);
		 }else{
			 msgs = DataCodecFactory.getInstance().decodeMsgUser(bytes);
		 }
		 Object handler = ioSession.getAttribute(HANDLER);
		 if(handler!=null){
			 ICallBackHandler callBack = (ICallBackHandler)handler;
			 for(Msg msg:msgs){
			 if(msg.getMsgHead().getErrorCode()!=SystemConstant.SUCCESS_CODE){
				 callBack.OnFail(msg, channel);
			 }else{
				 callBack.onSuccess(msg, channel);
			 }
			 }
		 }else{
			 ioSession.setAttribute(ShortResult, msgs);
		 }
		 channel.close();
	}
	@Override	
	public void sessionCreated(IoSession ioSession) throws Exception {
		// TODO Auto-generated method stub
		MinaChannel minaChannel = new MinaChannel(ioSession);
		ioSession.setAttribute(AbstractChannel.CHANNEL, minaChannel);
		LogSystem.info("mina client session create!"+ioSession.getId());
	}
	@Override
	public void sessionClosed(IoSession ioSession) throws Exception {
		LogSystem.info("sessionClose"+ioSession.getId());
	}
}
