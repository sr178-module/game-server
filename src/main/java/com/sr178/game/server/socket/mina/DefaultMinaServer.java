package com.sr178.game.server.socket.mina;

import java.io.IOException;
import java.net.InetSocketAddress;








import java.util.concurrent.Executors;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.sr178.game.framework.log.LogSystem;
import com.sr178.game.server.config.LocalTools;
import com.sr178.game.server.manager.IServer;
import com.sr178.game.server.socket.mina.codefactory.CodeFactory;
import com.sr178.game.server.socket.mina.heartbeat.HachiKeepAliveFilter;
import com.sr178.game.server.threadpool.ThreadPoolBean;


public class DefaultMinaServer implements IServer{
    private IoAcceptor acceptor;
	private String address;
	private int maxReadBufferSize;
	private IoHandler handler;
	private int ThreadCount;
	private ProtocolCodecFactory codeFactory;
	private int port;
	private boolean heartBeat;
	private ThreadPoolBean pool;
	public void restart() {
		shutdown();
		start();
	}
	public void shutdown() {
		acceptor.dispose();
	}
	public void start() {
		if(ThreadCount==0){
			ThreadCount = Runtime.getRuntime().availableProcessors()+1;
		}
		acceptor = new NioSocketAcceptor(ThreadCount); 
		DefaultIoFilterChainBuilder filterChain = acceptor.getFilterChain();
		//编码工厂
		if(codeFactory==null) {
			codeFactory = new CodeFactory();
		}
		//心跳测试
		if(heartBeat){
 		   filterChain.addLast("keep-alive", new HachiKeepAliveFilter());
		}
        //这里的顺序是相对于读取数据来说的    ioservice层的那一方位"head" 业务层为"tail"
		filterChain.addLast("codecfactory", new ProtocolCodecFilter(codeFactory));
		
//		MdcInjectionFilter mdcInjectionFilter = new MdcInjectionFilter();
//		filterChain.addLast("mdc", mdcInjectionFilter);
		//线程池
		if(pool!=null){
		    filterChain.addLast("pool", new ExecutorFilter(pool.getThreadPoolExecutor()));
		}else{
			filterChain.addLast("pool", new ExecutorFilter(Executors.newCachedThreadPool()));
		}
//		filterChain.addLast("logging", new LoggingFilter()); // 添加日志过滤器
		/** *********************** */
		//设置逻辑处理器
		acceptor.setHandler(handler);
		//设置读数据缓冲大小
		if(maxReadBufferSize!=0){
			acceptor.getSessionConfig().setReadBufferSize(maxReadBufferSize);
		}
		if(port==0){
		    port = LocalTools.getLocalConfig().getPort();
		}
		try {
			if(address==null){
				acceptor.bind(new InetSocketAddress(port));
			}else{
			    acceptor.bind(new InetSocketAddress(address,port));
			}
		} catch (IOException e) {
			LogSystem.error(e, "");
		}
		LogSystem.info("mina server start....");
		LogSystem.info("开启的线程数:"+(ThreadCount)+",address" + address+ "port=" + port + "maxReadBufferSize" + maxReadBufferSize);
	}
	
	public static void main (String[] args){
		ProtocolCodecFactory protocolCodecFactory = new CodeFactory();
		DefaultMinaHandler hadler = new DefaultMinaHandler();
		DefaultMinaServer minaServer = new DefaultMinaServer();
		minaServer.setCodeFactory(protocolCodecFactory);
		minaServer.setHandler(hadler);
		minaServer.setMaxReadBufferSize(1024);
		minaServer.setAddress("localhost");
		minaServer.setPort(12369);
		minaServer.start();
	}
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	public IoHandler getHandler() {
		return handler;
	}

	public void setHandler(IoHandler handler) {
		this.handler = handler;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public ProtocolCodecFactory getCodeFactory() {
		return codeFactory;
	}

	public void setCodeFactory(ProtocolCodecFactory codeFactory) {
		this.codeFactory = codeFactory;
	}

	public int getMaxReadBufferSize() {
		return maxReadBufferSize;
	}

	public void setMaxReadBufferSize(int maxReadBufferSize) {
		this.maxReadBufferSize = maxReadBufferSize;
	}
	public int getThreadCount() {
		return ThreadCount;
	}
	public void setThreadCount(int threadCount) {
		ThreadCount = threadCount;
	}
	public boolean isHeartBeat() {
		return heartBeat;
	}
	public void setHeartBeat(boolean heartBeat) {
		this.heartBeat = heartBeat;
	}
	public ThreadPoolBean getPool() {
		return pool;
	}
	public void setPool(ThreadPoolBean pool) {
		this.pool = pool;
	}
}
