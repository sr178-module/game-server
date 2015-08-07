package com.sr178.game.server.threadpool;

import java.util.LinkedList;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;


public class RejectThreadHandler implements RejectedExecutionHandler {
	public LinkedList<Runnable> list = new LinkedList<Runnable>();

	ThreadPoolExecutor pool;

	boolean stopThread = true;

	public RejectThreadHandler() {
		CycleThread cycleThread = new CycleThread();
		cycleThread.start();
	}

	public void rejectedExecution(Runnable arg0, ThreadPoolExecutor arg1) {
		if (pool == null) {
			pool = arg1;
		}

		synchronized (list) {
			list.add(arg0);
		}
	}

	public void stop() {
		stopThread = false;
	}

	class CycleThread extends Thread {
		@Override
		public void run() {
			while (stopThread) {
				if (pool != null) {
					if (!pool.isShutdown()) {
						if (list.size() > 0) {
							Runnable task = null;
							synchronized (list) {
								task = list.removeFirst();
							}
							pool.execute(task);
						}

					}
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
			}
		}
	}

}
