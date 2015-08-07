package com.sr178.game.server.bridge;

import java.util.List;

import com.sr178.game.msgbody.common.model.Msg;
import com.sr178.game.server.channel.Channel;

public interface IBridgeExit {

	public void sendMsg(Channel channel, Msg msg);
	
	public void sendMsgs(Channel channel, List<Msg> msgs);
}
