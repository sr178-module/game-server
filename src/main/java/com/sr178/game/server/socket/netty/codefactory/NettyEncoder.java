package com.sr178.game.server.socket.netty.codefactory;


import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import com.sr178.game.server.util.GZipUtil;

public class NettyEncoder extends OneToOneEncoder{
 

    @Override
    protected Object encode(
            ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (!(msg instanceof byte[])) {
            return msg;
        }
        byte[] body = (byte[]) msg;
	    	 boolean isCompress = false;
	 		 if(body.length>1024){
//	 			LogSystem.info("压缩前大小"+bytes.length);
	 			body = GZipUtil.compress(body);
//	 			LogSystem.info("压缩后大小"+bytes.length);
	 			isCompress= true;
	 	}
        //查看是否要压缩
        int length =  body.length;
        int dateLength = 5 + length;
        ChannelBuffer channelBuffer =  ChannelBuffers.buffer(dateLength);
        channelBuffer.writeInt(length);
        if(isCompress){
        	channelBuffer.writeByte(1);
        }else{
        	channelBuffer.writeByte(0);
        }
        channelBuffer.writeBytes(body);
        return channelBuffer;
    }
}
