package com.sr178.game.server.socket.mina.codefactory;



import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.sr178.game.server.util.GZipUtil;





public class Decoder extends CumulativeProtocolDecoder {
/**
 * 返回false = 通知父类继续去获取数据
 * 返回true =  通知父类重新执行一次doDecode()方法
 */
	@Override
	protected boolean doDecode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
//		in.order(ByteOrder.LITTLE_ENDIAN); //字节序, 

		//消息buf
//		IoBuffer buf = IoBuffer.allocate(128); //ServerConfig.MessageMaxByte 最大消息字节数
//		buf.order(ByteOrder.LITTLE_ENDIAN);
		if (in.remaining() > CodeFactory.HEAD_LENGTH) {
			in.mark();
			//5位的消息头
			int length = in.getInt();
			byte isCompress = in.get();
//			if (length > 128) {
//			}
			if (length > in.remaining()){
				in.reset();
				return false;
			}
//			LogSystem.info("sessionId为"+session.getId()+"有数据大小为:"+length);
			//复制一个完整消息
			
			byte[] bytes1 = new byte[length];
			in.get(bytes1, 0, length);
//			byte[] bytes2 = long2bytes(reciverTime,length);
//
//			byte[] byte3 = copybyte1Tobyte2(bytes1,bytes2);
			//设置收到该消息时的时间戳
			if(isCompress==1){
				bytes1 = GZipUtil.decompress(bytes1);
			}
			out.write(bytes1);
			//此种情况是粘包的情况 
			if(in.hasRemaining()){
				return true;
			}else{
			    return false;
			}
		} else {
			return false;
		}
	}
	
	public byte[] copybyte1Tobyte2(byte[] byte1,byte[] byte2){
		for(int i=0;i<byte1.length;i++){
			byte2[8+i] = byte1[i];
		}
		return byte2;
	}
	public byte[] long2bytes(long num,int arrayLength) {
		   byte[] b = new byte[8+arrayLength];
		   for (int i = 0; i < 8; i++) {
		    b[i] = (byte) (num >>> (56 - i * 8));
		   }
		   return b;
		}
}
