package com.sr178.game.server.threadpool;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class MyThreadFactory implements ThreadFactory {

	 static final AtomicInteger poolNumber = new AtomicInteger(1);
	    final ThreadGroup group;
	    final AtomicInteger threadNumber = new AtomicInteger(1);
	    String name;

		MyThreadFactory() {
	        SecurityManager s = System.getSecurityManager();
	        group = (s != null)? s.getThreadGroup() :
	                             Thread.currentThread().getThreadGroup();
	        name = "no name-" +
	                      poolNumber.getAndIncrement() +
	                     "-thread-";
	    }
	    public MyThreadFactory(String poolName) {
	        SecurityManager s = System.getSecurityManager();
	        group = (s != null)? s.getThreadGroup() :
	                             Thread.currentThread().getThreadGroup();
	        name = poolName +
	                      poolNumber.getAndIncrement() +
	                     "-thread-";
	    }
	    public Thread newThread(Runnable r) {
	        Thread t = new Thread(group, r,
	        		name + threadNumber.getAndIncrement(),
	                              0);
	        if (t.isDaemon())
	            t.setDaemon(false);
	        if (t.getPriority() != Thread.NORM_PRIORITY)
	            t.setPriority(Thread.NORM_PRIORITY);
	        return t;
	    }
	    
	    public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
}
