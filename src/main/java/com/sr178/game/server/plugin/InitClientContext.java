package com.sr178.game.server.plugin;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.DefaultChannelPipeline;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import com.sr178.game.framework.plugin.ISystemAppPlugin;
import com.sr178.game.framework.log.LogSystem;
import com.sr178.game.msgbody.common.model.Msg;
import com.sr178.game.msgbody.server.ReqRegisterChannelMsgBody;
import com.sr178.game.server.callback.ICallBackHandler;
import com.sr178.game.server.channel.NettyChannel;
import com.sr178.game.server.channel.manager.ClientConfigContext;
import com.sr178.game.server.config.LocalTools;
import com.sr178.game.server.constant.SystemConstant;
import com.sr178.game.server.msg.ChannelType;
import com.sr178.game.server.msg.MsgDispatchCenter;
import com.sr178.game.server.msg.ServerChannelManager;
import com.sr178.game.server.msg.ServerType;
import com.sr178.game.server.socket.netty.codefactory.NettyDecoder;
import com.sr178.game.server.socket.netty.codefactory.NettyEncoder;

public class InitClientContext implements ISystemAppPlugin {
   private List<ClientConfigContext> list;
   //等待连接建立时长10秒 10秒后没建完 则抛异常
   private static long TIME_OUT=10000; 
   private int successResponseCount;
   private int sendRequestCount;
   private int loseCount;
   private Map<String,ClientBootstrap> connectorMap = new HashMap<String,ClientBootstrap>();
   //通道的配置项绑定
   private static final String CC = "CC";

   private Timer timer;
    public void startup() throws Exception{
    	if(list!=null&&list.size()>0){
		   for(final ClientConfigContext clientConfig:list){
    	   ClientBootstrap bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool()));
           bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			@Override
			public ChannelPipeline getPipeline() throws Exception {
                DefaultChannelPipeline result = new DefaultChannelPipeline();
                result.addLast("frameDecoder",
                        new NettyDecoder(65535, 0, 4, 1, 5));
                result.addLast("frameEncoder", new NettyEncoder());
                result.addLast("handler", clientConfig.getHandler());
                return result;
			}
		}); 
        bootstrap.setOption("child.sendBufferSize", 2048);
        bootstrap.setOption("child.receiveBufferSize", 2048);
        bootstrap.setOption("child.tcpNoDelay", true); //关闭Nagle算法
        bootstrap.setOption("child.keepAlive", false);
        connectorMap.put(clientConfig.getConnectorId(), bootstrap);
		for(int i=0;i<clientConfig.getNums();i++){
		     connectGateWayServer(clientConfig,false);
		}
		long startTime = System.currentTimeMillis();
		while(true){
			  //监听所有连接都建立并获得注册成功消息完毕
			  if(successResponseCount==clientConfig.getNums()){
				  break;
			  }
			  if(System.currentTimeMillis()-startTime>TIME_OUT){
				  break;
			  }
			  Thread.sleep(1000);
		  }
		  if(successResponseCount!=clientConfig.getNums()){
			  throw new NullPointerException("channelType="+clientConfig.getChannelType()+"连接建立出错，需要建立连接数"+clientConfig.getNums()+",但成功的响应数量为:"+successResponseCount+"发送消息数量"+sendRequestCount+",失败响应数量"+loseCount);
		  }
		  LogSystem.info("channelType="+clientConfig.getChannelType()+",需要建立连接数"+clientConfig.getNums()+",成功的响应数量"+successResponseCount+"发送消息数量"+sendRequestCount+",失败响应数量"+loseCount);
		  successResponseCount=0;
		  sendRequestCount=0;
		  loseCount = 0;
		  }
	  }
    	//连接状态检测
		timer = new Timer();
		timer.schedule(new TimerTask(){
			@Override
			public void run() {
				try {
//					LogSystem.info("开始检测连接状态");
					checkConnectionStatus();
				} catch (Exception e) {
					LogSystem.error(e, "检测连接状态出错！！");
				}
				
			}
		 }
		, 60000l, 30000l);
	  LogSystem.info("初始化与网关服务器的连接完成！。");
    }
    
    
    private void checkConnectionStatus(){
    	for(final ClientConfigContext clientConfig:list){
    		int num = ServerChannelManager.getInstance().getChannelTypeCurrentConnectionsNum(ChannelType.getByName(clientConfig.getChannelType()));
//    		LogSystem.info("当前管道【"+clientConfig.getChannelType()+"】，数量为:"+num);
    		if(num<clientConfig.getNums()){
    			int needReconnectNum = clientConfig.getNums()-num;
    			LogSystem.warn("当前管道【"+clientConfig.getChannelType()+"】有异常的连接断开了~~本该有连接数为【"+clientConfig.getNums()+"】,缺失连接数为【"+needReconnectNum+"】");
    			for(int i=0;i<needReconnectNum;i++){
    			     connectGateWayServer(clientConfig,true);
    			}
    		}
    	}
    }
    
	public void shutdown() throws Exception {
		 
		
	}

	public List<ClientConfigContext> getList() {
		return list;
	}

	public void setList(List<ClientConfigContext> list) {
		this.list = list;
	}
	
	public void connectGateWayServer(ClientConfigContext clientConfig,boolean isReconnect){
		 InetSocketAddress socketAddress = new InetSocketAddress(clientConfig.getIp(),
				 clientConfig.getPort());
		 ChannelFuture future = connectorMap.get(clientConfig.getConnectorId()).connect(socketAddress);// 建立连接
		 Object o = future.awaitUninterruptibly().getChannel().getAttachment();
		 if(o!=null){
			 NettyChannel nettyChannel = (NettyChannel)o;
			 ServerChannelManager.getInstance().addChannel(LocalTools.getLocalConfig().getServerTypeEnum(),ChannelType.getByName(clientConfig.getChannelType()), nettyChannel);
			 sendMsgToGateWay(clientConfig,nettyChannel,isReconnect);
		 }else{
			 throw new NullPointerException("客户端连接建立不成功!");
		 }
	}
	/**
	 * 重连
	 * @param channel
	 */
	public void reconnectGateWayServer(final NettyChannel channel){
        //重连线程
        new Thread(new Runnable() {
			@Override
			public void run() {
				Object o = channel.getAttribute(CC);
				if(o!=null){
					LogSystem.warn("开始进行重连：-----------》----------->>>>");
					ClientConfigContext clientConfig = (ClientConfigContext)o;
					connectGateWayServer(clientConfig,true);
				}else{
					//需要重连的连接没有配置信息
					throw new RuntimeException("需要重连的连接没有配置信息");
				}
			}
		}).start();
	}
	
	private void sendMsgToGateWay(final ClientConfigContext clientConfig,final NettyChannel nettyChannel,boolean isReconnected){
		increaSendRequestCount();
		ReqRegisterChannelMsgBody body = new ReqRegisterChannelMsgBody();
		body.setChannelType(clientConfig.getChannelType());
		body.setServerType(LocalTools.getLocalConfig().getServerTypeEnum().name());
		MsgDispatchCenter.disPatchServerMsg(nettyChannel, ServerType.GATEWAY_SERVER, SystemConstant.REGESTER_CHANNEL, body,new ICallBackHandler() {
			@Override
			public void onSuccess(Msg msg, com.sr178.game.server.channel.Channel channel) {
				increaSuccessResponseCount();
				nettyChannel.addAttribute(CC, clientConfig);
			}
			@Override
			public void OnFail(Msg msg, com.sr178.game.server.channel.Channel channel) {
				increasloseCount();
			}
		});
	}
	
	private void increaSendRequestCount(){
		synchronized (connectorMap) {
			sendRequestCount++;
		}
	}
	private void increaSuccessResponseCount(){
		synchronized (connectorMap) {
			successResponseCount++;
		}
	}
	
	private void increasloseCount(){
		synchronized (connectorMap) {
			loseCount++;
		}
	}
	@Override
	public int spOrder() {
		return -9000;
	}
}
