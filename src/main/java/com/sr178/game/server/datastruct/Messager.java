package com.sr178.game.server.datastruct;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.sr178.game.framework.log.LogSystem;
import com.sr178.game.msgbody.common.model.Msg;

/**
 * 缓存数据
 * 
 * @author mengc
 * 
 */
public class Messager {

	private final LinkedList<Msg> msgList = new LinkedList<Msg>();

	/**
	 * 消息压栈
	 * 
	 * @param msg
	 */
	public void pushMsg(Msg msg) {
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
	public Msg popMsg() {
//		long now = System.currentTimeMillis();
		Msg msg = null;
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
	public void pushMsgList(List<Msg> cominMsgList) {
		if (cominMsgList == null || cominMsgList.size() == 0) {
			return;
		}
		for (Msg msg : cominMsgList) {
			pushMsg(msg);
		}
	}

	/*
	 * 取出msgList中的消息列表
	 * 
	 * @return
	 */
	public List<Msg> popMsgList(int maxNum) {


		List<Msg> reList = new ArrayList<Msg>();

		int size = msgList.size();

		if (size == 0) {
			return null;
		}

		if(size>maxNum){
			size = maxNum;
		}
//		LogSystem.info("取出消息条数" + size);
		Msg msg = null;
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
	public List<Msg> popAllMsgList() {


		List<Msg> reList = new ArrayList<Msg>();

		int size = msgList.size();

		if (size == 0) {
			return null;
		}
//		LogSystem.info("取出消息条数" + size);
		Msg msg = null;
		for (int i = 0; i < size; i++) {
			msg = popMsg();
			if (msg != null) {
				reList.add(msg);
			}
		}
		return reList;
	}
	 class TreadRunPush implements Runnable{
        private  Messager messager ;
        TreadRunPush(Messager messager){
        	this.messager =messager;
        }
		public void run() {
			// TODO Auto-generated method stub
			long now = System.currentTimeMillis();
			int i = 0;
			while(i<100000){
			messager.pushMsg(new Msg());
			i++;
//			LogSystem.info("push一个");

			}
			long end = System.currentTimeMillis();
			LogSystem.info("push时间:"+(end-now));

		}
		 
	 }
	 
	 class TreadRunPop implements Runnable{
	        private  Messager messager ;
	        TreadRunPop(Messager messager){
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
		 Messager messager = new Messager();
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
