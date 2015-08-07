package com.sr178.game.server.channel;

import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.session.IoSession;

import com.sr178.game.server.util.SequenseManager;



public class MinaChannel extends AbstractChannel {

	private final IoSession ioSession;

	private boolean isClosed = false;

	private String channelID;
	public MinaChannel(IoSession ioSession) {
		channelID = SequenseManager.getInstance().generateStaticseq()+"";
		this.ioSession = ioSession;
	}

	public void close() {
		if (isClosed) {
			return;
		}
		ioSession.close(true);
		isClosed = true;
	}

	public boolean isClosed() {
		if(!ioSession.isConnected()||isClosed){
			return true;
		}
		return false;
	}
	
	public void write(byte[] datas) {
		if (datas == null || datas.length == 0) {
			return;
		}
		if (!isClosed) {
			ioSession.write(datas);
		}
	}

	@Override
	public void writeAfterClose(byte[] datas) {
		ioSession.write(datas).addListener(new IoFutureListener<IoFuture>() {
			@Override
			public void operationComplete(IoFuture future) {
				future.getSession().close(true);
			}
		});
		
	}

	@Override
	public String getChannelId() {
		return channelID;
	}

	@Override
	public boolean isWriteAble() {
		return ioSession.isWriteSuspended();
	}

	
}
