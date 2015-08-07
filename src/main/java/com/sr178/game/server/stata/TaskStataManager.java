package com.sr178.game.server.stata;

import java.util.HashMap;
import java.util.Map;
/**
 * task性能统计管理器
 * @author ws
 *
 */
public class TaskStataManager {
  private static TaskStataManager taskStataManager;
  private static final ThreadLocal<Map<String,StatBean>> taskCacheStat = new ThreadLocal<Map<String,StatBean>>();

  private boolean isOpenStat;
  //统计数据map
  private Map<Integer,StatBean> map = new HashMap<Integer,StatBean>();
  
  //统计开始时间
  private long startTime;
  //统计结束时间
  private long endTime;
  //缓存查询统计
  private Map<Integer,Map<String,StatBean>> cacheStatMap =new HashMap<Integer,Map<String,StatBean>>();
  
  private TaskStataManager(){}
  public static TaskStataManager getInstance(){
	  if(taskStataManager==null){
		  taskStataManager = new TaskStataManager();
	  }
	  return taskStataManager;
  }
  /**
   * 初始化缓存统计
   */
  public void initCacheStat(){
	  if(isOpenStat){
	   taskCacheStat.set(new HashMap<String,StatBean>());
	  }
  }
  /**
   * 增加缓存对应的执行时间和返回的流量数
   * @param cacheName
   * @param time
   * @param bytes
   */
  public void increseCmdCodeNumCacheStat(String cacheName,long time,long bytes){
	  if(isOpenStat){
		  Map<String,StatBean> cacheStatmap= taskCacheStat.get();
		  if(cacheStatmap==null){
			  return;
		  }
		  StatBean stat = cacheStatmap.get(cacheName);
		  if(stat==null){
			  stat = new StatBean();
		  }
		  stat.times++;
		  stat.allTime = stat.allTime + time;
		  //这里代表从缓存中查询出来的数据量大小总和
		  stat.writeallbytes = stat.writeallbytes+bytes;
		  cacheStatmap.put(cacheName, stat);
//		  LogSystem.info("缓存统计数据添加cacheName="+cacheName+"times="+stat.times+",time="+time+",bytes="+bytes);
	  }
  }
  /**
   * 结束线程cache统计
   * @param cmdCode
   */
  public void endCacheStat(int cmdCode){
	  if(isOpenStat){
		  Map<String,StatBean> cacheStatmap= taskCacheStat.get();
		  if(cacheStatmap==null){
			  return;
		  }
		  synchronized (cacheStatMap) {
		  Map<String,StatBean> allCacheStat = cacheStatMap.get(cmdCode);
		  if(allCacheStat==null){
			  cacheStatMap.put(cmdCode, cacheStatmap);
		  }else{
		  for(String key:cacheStatmap.keySet()){
			  StatBean beAddStat = cacheStatmap.get(key);
			  StatBean stat = allCacheStat.get(key);
			 if(stat!=null){
				 stat.times=beAddStat.times+stat.times;
				 stat.allTime = beAddStat.allTime+stat.allTime;
				 stat.writeallbytes = beAddStat.writeallbytes+stat.writeallbytes;
			 }else{
				 allCacheStat.put(key, beAddStat);
			 }
//			 LogSystem.info("task缓存统计结束，cacheName="+key+",times="+stat.times+",allTime="+stat.allTime+",writeallbytes"+stat.writeallbytes);
		   }
		  }
	   }
		  taskCacheStat.remove();
    }
  }
  /**
   * 增加命令吗执行的次数及时长
   * @param cmdCode
   * @param time 
   */
	public void increseCmdCodeNum(int cmdCode, long time) {
		if (isOpenStat) {
			synchronized (map) {
				StatBean stat = map.get(cmdCode);
				if (stat == null) {
					stat = new StatBean();
				}
				stat.times++;
				stat.allTime = stat.allTime + time;
				map.put(cmdCode, stat);
			}
		}
	}
	/**
	 * 增加命令吗执行时写出去的字节流大小
	 * @param cmdCode
	 * @param bytes
	 */
	public void increaseCmdCodeBytes(int cmdCode, long bytes){
		if (isOpenStat) {
			synchronized (map) {
				StatBean stat = map.get(cmdCode);
				if (stat == null) {
					stat = new StatBean();
					map.put(cmdCode, stat);
				}else{
					stat.writeallbytes = stat.writeallbytes+bytes;
				}

			}
		}
	}
  //获取数据map
  public Map<Integer, StatBean> getMap() {
		return map;
  }
  //开启统计
  public void openStat(){
	  map.clear();
	  cacheStatMap.clear();
	  endTime = 0;
	  startTime = System.currentTimeMillis();
	  isOpenStat = true;
  }
  //关闭统计
  public void closeStat(){
	  endTime = System.currentTimeMillis();
	  isOpenStat = false;
  }
  //获取统计时长
  public long getStatTime(){
	  if(endTime-startTime < 0){
		  long time = System.currentTimeMillis();
		  return time - startTime ;
	  }
	  return (endTime-startTime);
  }
  
	public StatBean getStatBean(int cmdCode) {
		return map.get(cmdCode);
	}

	public long getStatBeanAllTime(int cmdCode) {
		return map.get(cmdCode).allTime;
	}

	public long getStatBeanWriteallbytes(int cmdCode) {
		return map.get(cmdCode).writeallbytes;
	}

	public int getStatBeanValueTimes(int cmdCode) {
		return map.get(cmdCode).times;
	}

	public boolean isOpenStat() {
		return isOpenStat;
	}

	public Map<Integer, Map<String, StatBean>> getCacheStatMap() {
		return cacheStatMap;
	}
	
	public int time(int value,String cacheName) {
		
		if(cacheStatMap.get(value).get(cacheName) == null){
			return 0;
		}
		int result = cacheStatMap.get(value).get(cacheName).times;
		return result;
	}
	
	public long allTime(int value,String cacheName) {
		return cacheStatMap.get(value).get(cacheName).allTime;
	}
	
	public long writeallbytes(int value,String cacheName) {
		return cacheStatMap.get(value).get(cacheName).writeallbytes;
	}
  
}
  class StatBean{
	  //执行次数
	  protected int times;
	  //执行的总时间累加
	  protected long allTime;
	  //流量总计字节数
	  protected long writeallbytes;
  }
