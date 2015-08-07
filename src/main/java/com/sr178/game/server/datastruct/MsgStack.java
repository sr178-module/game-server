package com.sr178.game.server.datastruct;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

import com.sr178.game.framework.log.LogSystem;


public class MsgStack<T> {
   private LinkedBlockingQueue<T> msgQueue = new LinkedBlockingQueue<T>();
   public int msgSize(){
	   return msgQueue.size();
   }
   /**
    * 将元素压入队列尾部
    * @param b
    */
   public void offer(T b){
	   msgQueue.offer(b);
   }
   
   /**
    * 获取并移除此队列的头部，在元素变得可用之前一直等待（如果有必要）。
    * @return
    */
   public T take(){
		   try {
			return msgQueue.take();
		} catch (InterruptedException e) {
			LogSystem.error(e, "");
		}
		return null;
   }
   /**
    * 获取但不移除此队列的头；如果此队列为空，则返回 null。
    * @return
    */
   public T peek(){
	   return msgQueue.peek();
   }
   /**
    * 获取并移除此队列的头，如果此队列为空，则返回 null。
    * @return
    */
   public T poll(){
	   return msgQueue.poll();
   }
   /**
    * 将队列中所有的元素都取出 并放入list中 返回取到的元素条数
    * @param list
    * @return
    */
   public int drainTo(Collection<T> list){
	   return msgQueue.drainTo(list);
   }
}
