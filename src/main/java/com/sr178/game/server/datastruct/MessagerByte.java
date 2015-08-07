package com.sr178.game.server.datastruct;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.sr178.game.framework.log.LogSystem;


/**
 * 缓存数据
 * 
 * @author mengc
 * 
 */
public class MessagerByte {

	private final LinkedList<byte[]> msgList = new LinkedList<byte[]>();

	/**
	 * 消息压栈
	 * 
	 * @param msg
	 */
	public void pushMsg(byte[] msg) {
//		long now = System.currentTimeMillis();
		if (msg == null) {
			return;
		}
		synchronized (msgList) {
			msgList.add(msg);
		}
//		long end = System.currentTimeMillis();
//		LogSystem.info("压入时间" + (end - now));

	}

	/**
	 * 弹出一个消息
	 * @return
	 */
	public byte[] popMsg() {
//		long now = System.currentTimeMillis();
		byte[] msg = null;
		synchronized (msgList) {
		if (!msgList.isEmpty()) {
				msg = msgList.remove();
			}
		}
//		long end = System.currentTimeMillis();
//		LogSystem.info("取出时间" + (end - now));
		return msg;
	}

	/**
	 * 消息列表压栈
	 * 
	 * @param msgList
	 */
	public void pushMsgList(List<byte[]> cominMsgList) {
		if (cominMsgList == null || cominMsgList.size() == 0) {
			return;
		}
		for (byte[] msg : cominMsgList) {
			pushMsg(msg);
		}
	}

	/*
	 * 取出msgList中的消息列表
	 * 
	 * @return
	 */
	public List<byte[]> popMsgList(int maxNum) {


		List<byte[]> reList = new ArrayList<byte[]>();

		int size = msgList.size();

		if (size == 0) {
			return null;
		}

		if(size>maxNum){
			size = maxNum;
		}
//		LogSystem.info("取出消息条数" + size);
		byte[] msg = null;
		for (int i = 0; i < size; i++) {
			msg = popMsg();
			if (msg != null) {
				reList.add(msg);
			}
		}
		return reList;
	}
	/*
	 * 取出所有msgList中的消息列表
	 * 
	 * @return
	 */
	public List<byte[]> popAllMsgList() {


		List<byte[]> reList = new ArrayList<byte[]>();

		int size = msgList.size();

		if (size == 0) {
			return null;
		}
//		LogSystem.info("取出消息条数" + size);
		byte[] msg = null;
		for (int i = 0; i < size; i++) {
			msg = popMsg();
			if (msg != null) {
				reList.add(msg);
			}
		}
		return reList;
	}
	 class TreadRunPush implements Runnable{
        private  MessagerByte messager ;
        TreadRunPush(MessagerByte messager){
        	this.messager =messager;
        }
		public void run() {
			// TODO Auto-generated method stub
			long now = System.currentTimeMillis();
			int i = 0;
			while(i<100000){
			messager.pushMsg(new byte[]{});
			i++;
//			LogSystem.info("push一个");

			}
			long end = System.currentTimeMillis();
			LogSystem.info("push时间:"+(end-now));

		}
		 
	 }
	 
	 class TreadRunPop implements Runnable{
	        private  MessagerByte messager ;
	        TreadRunPop(MessagerByte messager){
	        	this.messager =messager;
	        }
			public void run() {
				// TODO Auto-generated method stub
				long now = System.currentTimeMillis();
				int i = 0;
				while(i<100000){
				messager.popMsg();
				i++;
//				LogSystem.info("pop一个");
				}
				long end = System.currentTimeMillis();
				LogSystem.info("pop时间:"+(end-now));
			}
			 
		 }
	public static void main(String[] args) {
		 MessagerByte messager = new MessagerByte();
		 new Thread(messager.new TreadRunPush(messager)).start();
		 
		 new Thread(messager.new TreadRunPop(messager)).start();

		 new Thread(messager.new TreadRunPush(messager)).start();
		 new Thread(messager.new TreadRunPop(messager)).start();

		 new Thread(messager.new TreadRunPush(messager)).start();
		 new Thread(messager.new TreadRunPop(messager)).start();

		 new Thread(messager.new TreadRunPush(messager)).start();
		 new Thread(messager.new TreadRunPop(messager)).start();

		 new Thread(messager.new TreadRunPop(messager)).start();
		 new Thread(messager.new TreadRunPop(messager)).start();
	}

}
