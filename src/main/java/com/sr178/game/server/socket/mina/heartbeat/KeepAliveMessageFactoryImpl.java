package com.sr178.game.server.socket.mina.heartbeat;


import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;

public class KeepAliveMessageFactoryImpl implements KeepAliveMessageFactory {
	private static final byte int_req = -1;
	private static final byte int_rep = -2;
	private static final IoBuffer KAMSG_REQ = IoBuffer
			.wrap(new byte[] {0,0,0,1,0,int_req});
	private static final IoBuffer KAMSG_REP = IoBuffer
			.wrap(new byte[] { int_rep });
	
	    public Object getRequest(IoSession session) {   
	        return KAMSG_REQ.duplicate();   
	    }   

	    public Object getResponse(IoSession session, Object request) {   
	        return KAMSG_REP.duplicate();   
	    }   

	    public boolean isRequest(IoSession session, Object message) {
	    	//不支持客户端校验
	    	return false;
//	    	if(!(message instanceof IoBuffer))
//	    		return false;
//	    	IoBuffer realMessage = (IoBuffer)message;
//	    	if(realMessage.limit() != 1)
//	    		return false;
//	    	
//	        boolean result = (realMessage.get() == int_req);
//	        realMessage.rewind();
//	        return result;
	    }
	    
	    public boolean isResponse(IoSession session, Object message) {    
	    	if(!(message instanceof IoBuffer)){
	    		return false;
	    	}
	    	IoBuffer realMessage = (IoBuffer)message;
	    	if(realMessage.limit() != 1){
	    		return false;
	    	}
	        boolean result = (realMessage.get() == int_rep);   
	        realMessage.rewind();
	        return result;
	    }   
	}
