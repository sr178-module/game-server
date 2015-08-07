package com.sr178.game.server.stata;

public class MsgStataManager {
   private static MsgStataManager manager = null;
   private MsgStataManager(){}
   private boolean isOpenStata;
   private long msgNum;
   //统计开始时间
   private long startTime;
   //统计持续时间
   private long stataTime;
   public static MsgStataManager getInstance(){
	   if(manager==null){
		   manager = new MsgStataManager();
	   }
	   return manager;
   }
   
   public void increaseMsgNum(){
	   if(isOpenStata){
		   synchronized (this) {
			   msgNum++;
		}
	   }
   }
   /**
    * 开启统计
    */
   public void openMsgStata(){
	   if(isOpenStata){
		   return;
	   }else{
		   isOpenStata = true;
		   msgNum=0;
		   startTime = System.currentTimeMillis();
		   stataTime = 0;
	   }
   }
   /**
    * 关闭统计
    */
   public void closeMsgStata(){
	   isOpenStata = false;
	   stataTime = System.currentTimeMillis() - startTime;
   }
    /**
     * 获取统计持续的时间
     * @return
     */
	public long getStataTime() {
		return stataTime;
	}
   
	/**
	 * 获取获得的消息数量
	 * @return
	 */
	public long getMsgNum() {
		return msgNum;
	}

	public boolean isOpenStata() {
		return isOpenStata;
	}
	
}
