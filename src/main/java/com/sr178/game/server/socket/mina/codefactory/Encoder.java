package com.sr178.game.server.socket.mina.codefactory;


import java.util.Date;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.sr178.game.framework.log.LogSystem;
import com.sr178.game.server.util.GZipUtil;


public class Encoder extends ProtocolEncoderAdapter {

	public void encode(IoSession session, Object message,
			ProtocolEncoderOutput out) throws Exception {
		// TODO Auto-generated method stub
	 		 byte[] bytes = (byte[])message;
	 		 boolean isCompress = false;
	 		 if(bytes.length>10){
//	 			LogSystem.info("压缩前大小"+bytes.length);
	 			bytes = GZipUtil.compress(bytes);
//	 			LogSystem.info("压缩后大小"+bytes.length);
	 			isCompress= true;
	 		 }
	 		 IoBuffer buf = IoBuffer.allocate(bytes.length+CodeFactory.HEAD_LENGTH);//实例化buffer
	 		 byte b = (byte) (isCompress?1:0);
			 LogSystem.info("sessionId为"+session.getId()+"发送数据大小为:"+(bytes.length+5)+"time="+new Date());
	 		 buf.putInt(bytes.length);
	 		 buf.put(b);
	     	 buf.put(bytes);//将数据放入buffer
	     	 buf.flip();//指针归零
	     	 out.write(buf);//写出
	     	 buf.free();//释放
	         
	}
}
