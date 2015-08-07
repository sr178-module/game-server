package com.sr178.game.server.client.http;
//package com.easou.game.framework.client.http;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.lang.reflect.InvocationTargetException;
//import java.net.URL;
//import java.net.URLConnection;
//import java.util.ArrayList;
//import java.util.List;
//
//import com.easou.game.framework.constant.SystemConstant;
//import com.easou.game.framework.log.LogSystem;
//import com.easou.game.msgbody.commom.io.XIOFactoryManager;
//import com.easou.game.msgbody.commom.io.iface.IXInputStream;
//import com.easou.game.msgbody.commom.io.iface.IXOutStream;
//import com.easou.game.msgbody.commom.model.ICodeAble;
//import com.easou.game.msgbody.commom.model.Msg;
//import com.easou.game.msgbody.commom.model.MsgGroup;
//import com.easou.game.msgbody.server.CommomMsgBody;
//
///**
// * 服务器之间的http通讯
// * 
// * @author Administrator
// * 
// */
//public class HttpServersBridge {
//
//	public static List<Msg> sendMsgsToServer(String address,
//			List<Msg> msgList, Class<? extends ICodeAble> resBodyType) {
//		List<Msg> resList = null;
//		try {
//			URL url = new URL(address);
//			URLConnection httpURLConnection = url.openConnection();
//			httpURLConnection.setDoInput(true);
//			httpURLConnection.setDoOutput(true);
//			IXOutStream outStream = XIOFactoryManager.getIoFactoryByKey().getIXOutStream();
//			
//			outStream.setOutputStream(httpURLConnection.getOutputStream());
//
//			// 发送消息
//			sendMsgs(outStream, msgList);
//
//			InputStream inputStream = httpURLConnection.getInputStream();
//
//			resList = receiveMsgs(inputStream, resBodyType);
//		} catch (Exception e) {
//			LogSystem.error(e,"");
//		}
//		return resList;
//	}
//
//	private static void sendMsgs(IXOutStream outStream,
//			List<Msg> msgList) {
//		MsgGroup msgGroup = new MsgGroup();
//		List<Msg> msgsList = new ArrayList<Msg>();
//		msgsList.addAll(msgList);
//		msgGroup.setMsgsList(msgsList);
//		try {
//			msgGroup.encode(outStream);
//		} catch (IOException e) {
//			LogSystem.error(e,"");
//		}
//	}
//
//	private static List<Msg> receiveMsgs(InputStream inputStream,
//			Class<? extends ICodeAble> resBodyType) throws IOException,
//			IllegalArgumentException, SecurityException,
//			InstantiationException, IllegalAccessException,
//			InvocationTargetException, NoSuchMethodException {
//		
//		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
//		DataOutputStream cacheOutputStream = new DataOutputStream(
//				arrayOutputStream);
//
//		byte[] buff = new byte[256];
//		int size;
//		while ((size = inputStream.read(buff, 0, 256)) > 0) {
//			cacheOutputStream.write(buff, 0, size);
//		}
//
//		ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(
//				arrayOutputStream.toByteArray());
//		IXInputStream inputStream2 = XIOFactoryManager.getIoFactoryByKey().getIXInputStream();
//		inputStream2.setInputStream(arrayInputStream);
//		MsgGroup resGroup = new MsgGroup();
//		resGroup.decode(inputStream2);
//		List<Msg> resVector = resGroup.getMsgsList();
//		for (Msg msg : resVector) {
//			ICodeAble resBody = null;
//			// 如果是错误信息 则要把错误信息解析出来
//			if (msg.getMsgHead().getErrorCode() != SystemConstant.SUCCESS_CODE) {
//				resBodyType = CommomMsgBody.class;
//			} 
//			try {
//				resBody = msg.decodeBody(resBodyType);
//			} catch (Exception e) {
//				 LogSystem.error(e, "");
//			}
//			msg.setMsgBody(resBody);
//		}
//		List<Msg> resList = new ArrayList<Msg>();
//		resList.addAll(resVector);
//		return resList;
//	}
//}
