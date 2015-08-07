package com.sr178.game.server.threadpool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/**
 * 线程池
 * @author mengc
 *
 */
public class ThreadPoolBean {
	private ThreadPoolExecutor threadPoolExecutor;
	/**
	 * 不带名称的构造方法
	 * @param coreSize
	 * @param maxSize
	 * @param maxQueneLength
	 * @param aliveTime
	 */
//	public ThreadPoolBean(int coreSize, int maxSize, int maxQueneLength,
//			long aliveTime) {
//
//		RejectThreadHandler handler = new RejectThreadHandler();
//		threadPoolExecutor = new ThreadPoolExecutor(coreSize, maxSize,
//				aliveTime, TimeUnit.MILLISECONDS,
//				new ArrayBlockingQueue<Runnable>(maxQueneLength),new MyThreadFactory(),handler);
//	}
	/**
	 * 带名称的构造方法
	 * @param name
	 * @param coreSize
	 * @param maxSize
	 * @param maxQueneLength
	 * @param aliveTime
	 */
	public ThreadPoolBean(String name,int coreSize, int maxSize, int maxQueneLength,
			long aliveTime) {

		RejectThreadHandler handler = new RejectThreadHandler();
		threadPoolExecutor = new ThreadPoolExecutor(coreSize, maxSize,
				aliveTime, TimeUnit.MILLISECONDS,
				new ArrayBlockingQueue<Runnable>(maxQueneLength),new MyThreadFactory(name),handler);
	}
	
	public ThreadPoolExecutor getThreadPoolExecutor() {
		return threadPoolExecutor;
	}
}
