package com.sr178.game.server.client.socket;

import java.net.InetSocketAddress;

import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.sr178.game.server.socket.mina.codefactory.CodeFactory;

public class MinaClientFactory {
	private NioSocketConnector connector;
	private InetSocketAddress socketAddress;
	private MsgCodecType codecType;
	public MinaClientFactory(String ip,int port,MsgCodecType codecType){
		 connector = new NioSocketConnector();
		 connector.getFilterChain().addLast("codecfactory", new ProtocolCodecFilter(new CodeFactory()));; // 设置编码过滤器
		 MinaClientHandler clientHandler = new MinaClientHandler(codecType);
		 connector.setHandler(clientHandler);// 设置事件处理器
		 this.socketAddress = new InetSocketAddress(ip,
				    port);
		 this.codecType = codecType;
	}
	public MinaClient getClient(){
		return new MinaClient(connector,socketAddress,codecType);
	} 
}

