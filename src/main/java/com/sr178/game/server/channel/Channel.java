package com.sr178.game.server.channel;



public interface Channel {

	public void write(byte[] datas);
	
	public void writeAfterClose(byte[] datas);
	
	public boolean isClosed();

	public void close();

	public int getProtocolType();
	
	public void setProtocolType(int protocolType);

	public void addAttribute(String key, Object value);

	public Object getAttribute(String key);
	
	public void clearAttribute();
	
	public int getClientType();
	
	public String getChannelId();
	
	public boolean isWriteAble();
}
