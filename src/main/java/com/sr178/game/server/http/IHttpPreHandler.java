package com.sr178.game.server.http;

import org.jboss.netty.channel.Channel;
/**
 * 拦截器
 * @author Administrator
 *
 */
public interface IHttpPreHandler {
	/**
	 * 
	 * @param request
	 * @param channel
	 * @return
	 */
	public void preHandler(Request request, Channel channel);
	
	/**
	 * 获取拦截的地址前缀  没有什么*不*的 前缀相等就拦截
	 * @return
	 */
	public String getInterceptorUrl();
}
