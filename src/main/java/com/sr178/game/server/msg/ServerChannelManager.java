package com.sr178.game.server.msg;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sr178.game.framework.log.LogSystem;
import com.sr178.game.msgbody.common.codec.DataCodecFactory;
import com.sr178.game.msgbody.common.model.Msg;
import com.sr178.game.server.channel.AbstractChannel;
import com.sr178.game.server.channel.Channel;
import com.sr178.game.server.concurrent.PaddedAtomicLong;
import com.sr178.game.server.monitor.MonitorService;

public class ServerChannelManager {
   private static ServerChannelManager manager = new ServerChannelManager();
   private static PaddedAtomicLong loopCnt = new PaddedAtomicLong();
	
   //通道类型 --  通道里的管道id -- 通道里的管道
   private Map<ChannelType,Map<Integer,Channel>> channelIdMap = new ConcurrentHashMap<ChannelType,Map<Integer,Channel>>();
   //channelid与序号对应关系
   private Map<String,Integer> channleIdToIndex = new ConcurrentHashMap<String,Integer>();
//   private List<String> nullSids = new ArrayList<String>();
   private ServerChannelManager(){
   }
   public static ServerChannelManager getInstance(){
	   return manager;
   }
   /**
    * 获取当前管道类型下的连接数
    * @param type
    * @return
    */
   public int getChannelTypeCurrentConnectionsNum(ChannelType type){
	   Map<Integer,Channel> map = channelIdMap.get(type);
	   if(map!=null){
		   return map.size();
	   }
	   return 0;
   }
   /**
    * 添加连接
    * @param type
    * @param channel
    */
   public void addChannel(ServerType serverType,ChannelType type,Channel channel){
	   synchronized (channelIdMap) {
		int currentSize = 0;
		if(channelIdMap.containsKey(type)){
			Map<Integer,Channel> map = channelIdMap.get(type);
			int size = map.size();
			channelIdMap.get(type).put(size, channel);
			channleIdToIndex.put(channel.getChannelId(), size);
			currentSize = size+1;
		}else{
			Map<Integer,Channel> map = new ConcurrentHashMap<Integer,Channel>();
			map.put(0, channel);
			channleIdToIndex.put(channel.getChannelId(), 0);
			channelIdMap.put(type, map);
			currentSize = 1;
			//初始化服务器对应的管道类型
			ServerToChannelManager.getInstance().addServerTypeChannle(serverType, type);
		}
		channel.addAttribute(AbstractChannel.SERVER_TYPE, serverType);
		channel.addAttribute(AbstractChannel.CHANNEL_TYPE, type);
		LogSystem.info("添加连接成功，当前连接数为===="+currentSize);
	  }
   }
   /**
    * 获取一个连接  分布式算法
    * @param type
    * @return
    */
   public Channel getChannel(ChannelType type){
	   Map<Integer,Channel> map = channelIdMap.get(type);
	   Channel channel = null;
	   int channelCount = 0;
	   if(map!=null){
		   channelCount = map.size(); 
	   }
	   if(channelCount>0){
			int nullCount = 0;
			int busyCount = 0;
			while(true){
				if(nullCount > channelCount){
					LogSystem.error(new NullPointerException(""),"获取服务器连接失败，channelType:" + type);
					break;
				}
				int idx = (int)(loopCnt.getAndIncrement() % map.size());
				channel =map.get(idx);
				if(channel != null && !channel.isClosed()){
//					LogSystem.info("使用"+channel.getChannelId()+"进行发送，index="+idx+",channelType="+type);
					if(channel.isWriteAble()){
						return channel;
					}else{
						busyCount++;
						//轮训一圈后还是没有空闲的连接  则直接返回繁忙的连接
						if(busyCount>=channelCount){
							LogSystem.warn("connection size="+channelCount+",all connection is busy,please add connection count!!");
							return channel;
						}
						continue;
					}
				}else{
					removeChannel(channel);
//					nullSids.add(idx+"");
//					LogSystem.warn("空SID数量：" + nullSids.size());
				}
				nullCount++;
			}
	   }
	   throw new NullPointerException("获取服务器连接失败，channelType:" + type);
   }
   //往服务器通道发送消息
   public void sendMsgToChannel(ChannelType type,List<Msg> msgsList){
	   try {
			if(msgsList.size()>0){
			   Channel channel = ServerChannelManager.getInstance().getChannel(type);
			   if(channel!=null){
				    byte[] datas=null;
				    LogSystem.debug("开始发送消息"+msgsList.get(0).getMsgHead().getCmdCode()+",Time="+System.currentTimeMillis());
					datas = DataCodecFactory.getInstance().encodeMsgServer(msgsList);
					MonitorService.getInstance().markServerOutcomingBandwidth(datas.length);
				    channel.write(datas);
			    }
			 }
			}catch (Throwable e) {
				LogSystem.error(e, "");
			}
		}
   /**
    * 清除一个死亡连接的管道  需要重新排序
    * @param channel
    */
   public void removeChannel(Channel channel){
	    synchronized (channelIdMap) {
			ChannelType channelType = (ChannelType)channel.getAttribute(AbstractChannel.CHANNEL_TYPE);
			Map<Integer,Channel> map = channelIdMap.get(channelType);
			Map<Integer,Channel> tempMap = new ConcurrentHashMap<Integer,Channel>();
			Integer index = channleIdToIndex.remove(channel.getChannelId());
			if(index!=null){
				map.remove(index);
				if(map!=null&&map.size()>0){
					for(Channel itChannel:map.values()){
						channleIdToIndex.remove(itChannel.getChannelId());
						int size = tempMap.size();
						tempMap.put(size, itChannel);
						channleIdToIndex.put(itChannel.getChannelId(), size);
					}
					channelIdMap.put(channelType, tempMap);
				}
				LogSystem.info(channelType+"删除连接"+channel.getChannelId()+",还剩下连接数"+tempMap.size());
			}
		}
   }
}
