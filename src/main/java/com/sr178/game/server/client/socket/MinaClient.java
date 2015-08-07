package com.sr178.game.server.client.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;







import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.sr178.game.framework.log.LogSystem;
import com.sr178.game.msgbody.common.codec.DataCodecFactory;
import com.sr178.game.msgbody.common.model.Msg;
import com.sr178.game.server.callback.ICallBackHandler;

public class MinaClient {
	private IoSession ioSession;
	private NioSocketConnector connector;
	private InetSocketAddress socketAddress;
	private MsgCodecType codecType;
	
	public MinaClient(NioSocketConnector connector,InetSocketAddress socketAddress,MsgCodecType codecType) {
		 this.connector = connector;
		 this.socketAddress = socketAddress;
		 this.codecType = codecType;
	}
	
	private void connect(){
		synchronized (this) {
			if(ioSession==null){
			   ConnectFuture  cf = connector.connect(socketAddress);// 建立连接
			   cf.awaitUninterruptibly();// 等待连接创建完成
			   ioSession = cf.getSession();
			}
		}

	}
	public void close(){
		ioSession.close(true);
	}
	public boolean isCanOperator(){
		if(ioSession.isReaderIdle()||ioSession.isWriterIdle()){
			return false;
		}
		return true;
	}
	public boolean isConnected(){
		return ioSession.isConnected();
	}
	/**
	 * 发送消息  
	 * @param msg
	 * @param callBackHandler
	 * @throws IOException
	 */
	public void sendMsg(Msg msg,ICallBackHandler callBackHandler){
		connect();
		byte[] datas =null;
		if(codecType==MsgCodecType.User){
			datas = DataCodecFactory.getInstance().encodeMsgUser(msg);
		}else{
			datas = DataCodecFactory.getInstance().encodeMsgServer(msg);
		}
		if(callBackHandler!=null){
			ioSession.setAttribute(MinaClientHandler.HANDLER, callBackHandler);
		}
		if(isConnected()&&isCanOperator()){
			 ioSession.write(datas);
			 ioSession.getCloseFuture().awaitUninterruptibly(3000);
		}else{
			close();
			LogSystem.error(new RuntimeException("socket已经断开！请重新连接！"), "");
		}
	}
	/**
	 * 发送消息 发完后不需要响应 发送成功了就返回了
	 * @param msg
	 * @param callBackHandler
	 * @throws IOException
	 */
	public void sendMsgWithNoReplay(Msg msg){
		connect();
		byte[] datas =null;
		if(codecType==MsgCodecType.User){
			
			datas = DataCodecFactory.getInstance().encodeMsgUser(msg);
		}else{
			datas = DataCodecFactory.getInstance().encodeMsgServer(msg);
		}
		if(isConnected()&&isCanOperator()){
			 ioSession.write(datas).awaitUninterruptibly(3000);
			 close();
		}else{
			close();
			LogSystem.error(new RuntimeException("socket已经断开！请重新连接！"), "");
		}
	}
	/**
	 * 短连接 阻塞发送消息 消息返回后将会关闭连接
	 * @param msg
	 * @param callBackHandler
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public Msg sendMsg(Msg msg){
		connect();
		byte[] datas =null;
		if(codecType==MsgCodecType.User){
			datas = DataCodecFactory.getInstance().encodeMsgUser(msg);
		}else{
			datas = DataCodecFactory.getInstance().encodeMsgServer(msg);
		}
		if(isConnected()&&isCanOperator()){
			  ioSession.write(datas);
			  ioSession.getCloseFuture().awaitUninterruptibly(3000);
			  Object o =  ioSession.getAttribute(MinaClientHandler.ShortResult);
			  if(o!=null){
				  List<Msg> msgs = (List<Msg>)o;
				  return msgs.get(0);
			  }
		}else{
			close();
			LogSystem.error(new RuntimeException("socket已经断开！请重新连接！"), "");
		}
		return null;
	}
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		close();
	}
}

