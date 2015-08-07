package com.sr178.game.server.client.socket;
//package com.easou.game.framework.client.socket;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.lang.reflect.InvocationTargetException;
//import java.net.InetSocketAddress;
//import java.util.ArrayList;
//import java.util.List;
//
//
//import org.apache.mina.core.future.ConnectFuture;
//import org.apache.mina.core.future.ReadFuture;
//import org.apache.mina.core.service.IoConnector;
//import org.apache.mina.core.session.IoSession;
//import org.apache.mina.filter.codec.ProtocolCodecFilter;
//import org.apache.mina.filter.logging.LoggingFilter;
//import org.apache.mina.transport.socket.nio.NioSocketConnector;
//
//import com.easou.game.framework.client.socket.codec.CodeFactoryForClient;
//import com.easou.game.framework.constant.SystemConstant;
//import com.easou.game.framework.log.LogSystem;
//import com.easou.game.msgbody.commom.io.XIOFactoryManager;
//import com.easou.game.msgbody.commom.io.iface.IXInputStream;
//import com.easou.game.msgbody.commom.io.iface.IXOutStream;
//import com.easou.game.msgbody.commom.model.ICodeAble;
//import com.easou.game.msgbody.commom.model.Msg;
//import com.easou.game.msgbody.commom.model.MsgGroup;
//import com.easou.game.msgbody.commom.model.MsgHead;
//import com.easou.game.msgbody.server.CommomMsgBody;
//
//public class SocketClient{
//	private IoConnector connector;
//	private String ip;
//	private int port;
//	private IoSession session;
//	private boolean isConnected=false;
//	//回显时间10秒
//	private static final long TIME_OUT = 5000;
//	public SocketClient(String ip,Integer port) {
//		this.ip = ip;
//		this.port = port;
//	}
//	
//	private void connect() {
//		if (!isConnected) {
//			    connector = new NioSocketConnector();
//			    connector.getSessionConfig().setReadBufferSize(512);
//			    connector.setConnectTimeoutMillis(TIME_OUT);
//			    connector.getFilterChain().addLast("logger", new LoggingFilter());
//			    connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new CodeFactoryForClient()));
//				connector.getSessionConfig().setUseReadOperation(true);
//				ConnectFuture future =connector.connect(new InetSocketAddress(ip,port));
//				future.awaitUninterruptibly();
//			    session =future.getSession();
//			    isConnected = true;
//		}
//	}
//
//	/**
//	 * 发送socket消息 不用回应
//	 * @param msgList
//	 */
//	public void sendMsgsToServerWithNoAnswer(List<Msg> msgList) {
//		connect();
//  		// 发送消息
//		sendMsgs(msgList);
//		close();
//	}
//	
//	/**
//	 * 发送socket消息 不用回应
//	 * @param msgList
//	 */
//	public void sendMsgToServerWithNoAnswer(Msg msg) {
//		 List<Msg> msglist = new ArrayList<Msg>();
//		 msglist.add(msg);
//		 sendMsgsToServerWithNoAnswer(msglist);
//	}
//	/**
//	 * 发送socket消息 有回应
//	 * @param msgList
//	 */
//	public List<Msg> sendMsgsToServer(List<Msg> msgList,
//			Class<? extends ICodeAble> resBodyType) {
//		//连接
//		connect();
//		List<Msg> reList = null;
//		try {
//			// 发送消息
//			sendMsgs(msgList);
//			reList = receiveMsgs(resBodyType);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			LogSystem.error(e, "");
//		} catch (IllegalArgumentException e) {
//			// TODO Auto-generated catch block
//			LogSystem.error(e, "");
//		} catch (SecurityException e) {
//			// TODO Auto-generated catch block
//			LogSystem.error(e, "");
//		} catch (InstantiationException e) {
//			// TODO Auto-generated catch block
//			LogSystem.error(e, "");
//		} catch (IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			LogSystem.error(e, "");
//		} catch (InvocationTargetException e) {
//			// TODO Auto-generated catch block
//			LogSystem.error(e, "");
//		} catch (NoSuchMethodException e) {
//			// TODO Auto-generated catch block
//			LogSystem.error(e, "");
//		}
//		close();
//		return reList;
//	}
//	
//	/**
//	 * 发送一条消息 接受一条消息
//	 * @param msg
//	 * @param resBodyType
//	 * @return
//	 */
//	public Msg sendMsgsToServer(Msg msg,
//			Class<? extends ICodeAble> resBodyType){
//		List<Msg> msgList = new ArrayList<Msg>();
//		msgList.add(msg);
//		List<Msg> list = sendMsgsToServer(msgList, resBodyType);
//		if(list!=null&&list.size()>0){
//			return list.get(0);
//		}
//		return null;
//	}
//
//	private void sendMsgs(List<Msg> msgList) {
//		ByteArrayOutputStream cacheBOutputStream = new ByteArrayOutputStream();
//		IXOutStream cacheStream = XIOFactoryManager.getIoFactoryByKey().getIXOutStream();
//		cacheStream.setOutputStream(cacheBOutputStream);
//		MsgGroup msgGroup = new MsgGroup();
//		List<Msg> msgsList = new ArrayList<Msg>();
//		msgsList.addAll(msgList);
//		msgGroup.setMsgsList(msgsList);
//		try {
//			msgGroup.encode(cacheStream);
////			long startWriteTime = System.currentTimeMillis();
//            session.write(cacheBOutputStream.toByteArray()).awaitUninterruptibly();
////            long endWriteTime = System.currentTimeMillis();
////            LogSystem.info("写数据用的时间为"+(endWriteTime-startWriteTime));
//		} catch (IOException e) {
//			LogSystem.error(e, "");
//		}
//	}
//
//	/**
//	 *接收消息
//	 * @param inputStream
//	 * @param resBodyType
//	 * @return
//	 * @throws IOException
//	 * @throws IllegalArgumentException
//	 * @throws SecurityException
//	 * @throws InstantiationException
//	 * @throws IllegalAccessException
//	 * @throws InvocationTargetException
//	 * @throws NoSuchMethodException
//	 */
//	public List<Msg> receiveMsgs(Class<? extends ICodeAble> resBodyType) throws IOException,
//			IllegalArgumentException, SecurityException,
//			InstantiationException, IllegalAccessException,
//			InvocationTargetException, NoSuchMethodException {
////		long startReciveTime = System.currentTimeMillis();
//		ReadFuture readFuture = session.read().awaitUninterruptibly();
//        Object returnMsg = readFuture.getMessage();
////        long endReciveTime = System.currentTimeMillis();
////        LogSystem.info("收数据花的时间为"+(endReciveTime-startReciveTime));
//        byte[]  bytes = (byte[])returnMsg;
//		ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(bytes);
//		IXInputStream inputStream2 = XIOFactoryManager.getIoFactoryByKey().getIXInputStream();
//		inputStream2.setInputStream(arrayInputStream);
//		MsgGroup resGroup = new MsgGroup();
//		resGroup.decode(inputStream2);
//		//关闭临时流
//		inputStream2.close();
//		List<Msg> resVector = resGroup.getMsgsList();
//		for (Msg msg : resVector) {
//			ICodeAble resBody = null;
//			//如果是错误信息 则要把错误信息解析出来
//			if(msg.getMsgHead().getErrorCode()!=SystemConstant.SUCCESS_CODE){
//				resBodyType =CommomMsgBody.class;
//			}
//			resBody = msg.decodeBody(resBodyType);
//			msg.setMsgBody(resBody);
//		}
//		List<Msg> resList = new ArrayList<Msg>();
//		resList.addAll(resVector);
//		return resList;
//	}
//
//	public void close(){
//			session.close(true);
//			session.getService().dispose();
//			isConnected=false;
//	}
//	
//	public Msg getMsg(int cmdCode,int fromType,String fromId,int toType,String toId,String userSequence,ICodeAble msgBody){
//		    Msg msgTT = new Msg();
//			MsgHead msgHead2 = new MsgHead();
//			msgHead2.setToID(toId);
//			msgHead2.setFromType(fromType);
//			msgHead2.setToType(toType);
//			msgHead2.setCmdCode(cmdCode);
//			msgHead2.setMsgType(MsgHead.TYPEOFREQUEST);
//			msgHead2.setFromID(fromId);
//			msgHead2.setUserSequense(userSequence);
//			msgTT.setMsgBody(msgBody);
//			msgTT.setMsgHead(msgHead2);
//			return msgTT;
//	  } 
//
//	public static void main(String[] args) {
//		byte[] b1 = new byte[]{0,0,0,1};
//		byte[] b2 = new byte[]{0,0,0,1};
//		
//        System.out.println(b1.hashCode());
//        System.out.println(b2.hashCode());
//
//	}
//}
