package com.sr178.game.server.socket.mina;




import java.net.InetSocketAddress;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.sr178.game.framework.log.LogSystem;
import com.sr178.game.server.bridge.IBridgeEntry;
import com.sr178.game.server.channel.AbstractChannel;
import com.sr178.game.server.channel.MinaChannel;
import com.sr178.game.server.constant.SystemConstant;

public class DefaultMinaHandler extends IoHandlerAdapter {
	public static final String CHANNEL = "CHANNEL";

	public static final String IS_FIRST_EXCEPTION = "IS_FIRST_EXCEPTION";
	
	public static final String IS_FIRST_REQUEST = "IS_FIRST_REQUEST";
	
	private IBridgeEntry bridgeEntry;
	
	public IBridgeEntry getBridgeEntry() {
		return bridgeEntry;
	}

	public void setBridgeEntry(IBridgeEntry bridgeEntry) {
		this.bridgeEntry = bridgeEntry;
	}
	@Override
	public void exceptionCaught(IoSession ioSession, Throwable arg1)
			throws Exception {
//		LogSystem.error(new Exception(arg1), "socket异常");
		ioSession.close(true);
	}
	@Override
	public void messageReceived(IoSession ioSession, Object arg1) throws Exception {
		byte[] bytes = (byte[])arg1;
		MinaChannel channel = (MinaChannel) ioSession.getAttribute(CHANNEL);
		bridgeEntry.receivedData(channel, bytes);
	}
	@Override	
	public void sessionCreated(IoSession ioSession) throws Exception {
		MinaChannel channel = new MinaChannel(ioSession);
		channel.setProtocolType(SystemConstant.SOCKET_CONNECT);
		channel.addAttribute(IS_FIRST_REQUEST, true);
		ioSession.setAttribute(CHANNEL, channel);
		String clientIP = ((InetSocketAddress)ioSession.getRemoteAddress()).getAddress().getHostAddress();
		channel.addAttribute(AbstractChannel.IP, clientIP);
		channel.addAttribute(AbstractChannel.IS_VERIFY_DATA, false);
	}

	@Override
	public void sessionClosed(IoSession ioSession) throws Exception {
		LogSystem.info("sessionClose"+ioSession.getId());
	}
}
