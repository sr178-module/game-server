package com.sr178.game.server.bridge;


import com.sr178.game.server.channel.Channel;

public interface IBridgeEntry {

	public void receivedData(Channel channel, byte[] datas) throws Exception;

}
