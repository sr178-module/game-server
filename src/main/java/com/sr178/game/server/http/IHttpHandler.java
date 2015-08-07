package com.sr178.game.server.http;

import org.jboss.netty.channel.Channel;

public interface IHttpHandler {
    /**
     * 
     * @param uri
     * @param request
     * @param channel
     */
    Object handle(Request request, Channel channel);
    /**
     * 映射的地址
     */
    String getMappingUrl();
}
