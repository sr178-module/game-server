package com.sr178.game.server.msg;

import com.sr178.game.framework.plugin.IAppPlugin;


public class ServerMsgSender implements IAppPlugin {
	private long sleepTime = 10;
	private int coreSize = 8 ;
//	private ScheduledThreadPoolExecutor scheduledExec;
	public long getSleepTime() {
		return sleepTime;
	}
	public void setSleepTime(long sleepTime) {
		this.sleepTime = sleepTime;
	}
	
	public int getCoreSize() {
		return coreSize;
	}
	public void setCoreSize(int coreSize) {
		this.coreSize = coreSize;
	}
	@Override
	public void shutdown() throws Exception{
	}
	@Override
	public void startup() throws Exception{
//		scheduledExec = new ScheduledThreadPoolExecutor(coreSize,
//                new ThreadFactory(){
//                    private final PaddedAtomicInteger idCounter = new PaddedAtomicInteger();
//                    @Override
//                    public Thread newThread(Runnable r){
//                        Thread result = new Thread(r, "Send-Server-to-Server-Msg-thread"
//                                + idCounter.incrementAndGet());
//                        result.setPriority(Thread.MAX_PRIORITY);
//                        return result;
//                    }
//                });
//        scheduledExec.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
//        scheduledExec.scheduleAtFixedRate(new Runnable() {
//			@Override
//			public void run() {
//				ServerMsgManager.getInstance().handleEvent();
//			}
//		}, 500, sleepTime, TimeUnit.MILLISECONDS);
//		LogSystem.info("服务器下行消息发送器启动！消息发送间隔时间"+sleepTime+",coreSize="+coreSize);
	}
	@Override
	public int cpOrder() {
		return 0;
	}
}