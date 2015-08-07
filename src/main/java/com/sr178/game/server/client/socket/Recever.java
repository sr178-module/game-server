package com.sr178.game.server.client.socket;
//package com.easou.game.framework.client.socket;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.util.List;
//
//import com.easou.game.framework.constant.SystemConstant;
//import com.easou.game.msgbody.commom.io.XIOFactoryManager;
//import com.easou.game.msgbody.commom.io.iface.IXInputStream;
//import com.easou.game.msgbody.commom.model.Msg;
//import com.easou.game.msgbody.commom.model.MsgGroup;
//import com.easou.game.msgbody.server.CommomMsgBody;
//
//public class Recever implements Runnable{
//	DataInputStream di;
//    public Recever(DataInputStream in){
//    	this.di = in;
//    }
//	@Override
//	public void run() {
//		// TODO Auto-generated method stub
//			 while(true){
//				 List<Msg> list = null;
//				 try {
//					Thread.sleep(100);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				 if((list=doExcute(di))!=null){
//					 for(Msg msg:list){
//						 if(msg.getMsgHead().getErrorCode()==SystemConstant.FAIL_CODE){
//							 CommomMsgBody msgBody = msg.decodeBody(CommomMsgBody.class);
//							 System.out.println(Thread.currentThread().getName()+"收到错误响应消息，错误描述:"+msgBody.getErrorDescription());
//						 }
////						 System.out.println(Thread.currentThread().getName()+"收到命令码为:"+msg.getMsgHead().getCmdCode()+" 的消息，消息体大小为:"+msg.getMsgHead().getSizeOfMsgBody()+" ===== "+msg.getData().length);
//					 }
//				 }
//			 }
//		} 
//	
//	
//	public List<Msg> doExcute(DataInputStream in){
//		try {
//			if(in.available()>=4){
//				in.mark(4);
//				int len = in.readInt();
//				if(in.available()>=len){
//					byte[] bytes = new byte[len];
//					in.read(bytes, 0, len);
//					ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(bytes);
//					IXInputStream inputStream2 = XIOFactoryManager.getIoFactoryByKey().getIXInputStream();
//					inputStream2.setInputStream(arrayInputStream);
//					MsgGroup resGroup = new MsgGroup();
//					resGroup.decode(inputStream2);
//					return resGroup.getMsgsList();
//				}else{
//					in.reset();
//					return null;
//				}
//			} 
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;
//	}
//	
//	public byte[] doExcuteByte(DataInputStream in){
//		try {
//			if(in.available()>=4){
//				in.mark(4);
//				int len = in.readInt();
//				if(in.available()>=len){
//					byte[] bytes = new byte[len];
//					in.read(bytes, 0, len);
//					return bytes;
//				}else{
//					in.reset();
//					return null;
//				}
//			} 
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;
//	}
//	
//	public void runByte() {
//		// TODO Auto-generated method stub
//			 while(true){
//				 byte[] bytes = null;
//				 try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				 if((bytes=doExcuteByte(di))!=null){
//					 System.out.println("收到的bytes大小为："+bytes.length);
//				 }
//			 }
//		} 
//	public static void main(String[] args) {
//		ByteArrayOutputStream arrayOutPutStream = new ByteArrayOutputStream();
//		DataOutputStream out = new DataOutputStream(arrayOutPutStream);
//		try {
//			out.writeInt(8);
//			out.writeInt(4);
//			out.writeInt(4);
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		
//		
//		byte[] bytes = arrayOutPutStream.toByteArray();
//		ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(bytes);
//		DataInputStream di = new DataInputStream(arrayInputStream);
//		Recever recever = new Recever(di);
//		while(true){
//			try {
//				Thread.sleep(1000);
//				recever.runByte();
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}
//}
