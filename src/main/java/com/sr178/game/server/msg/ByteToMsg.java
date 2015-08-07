package com.sr178.game.server.msg;


import java.util.List;

import com.sr178.game.msgbody.common.codec.DataCodecFactory;
import com.sr178.game.msgbody.common.model.Msg;
import com.sr178.game.server.channel.Channel;

/**
 * 转换器 byte--->msg
 * @author mengc
 *
 */
public class ByteToMsg {
	
	private byte[] datas;
	
	private List<Msg> msgVector;
	
	public ByteToMsg(byte[] datas) {
		super();
		this.datas = datas;
	}

	public byte[] getDatas() {
		return datas;
	}

	public List<Msg> getMsgVector() {
		return msgVector;
	}

	public void setMsgVector(List<Msg> msgVector) {
		this.msgVector = msgVector;
	}
//	/**
//	 * 解消息头
//	 * @param channel
//	 */
//	public boolean parseByte(Channel channel){
//		byte[] reqDatas = datas;
//		
//		boolean isDecodeSucess = true;
//		IXInputStream inputStream = XIOFactoryManager.getIoFactoryByKey().getIXInputStream();
//		ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(reqDatas);
//		inputStream.setInputStream(arrayInputStream);
//		MsgGroup msgGroup = new MsgGroup();
//		try {
//			    //解码消息收到的时间
//			    msgGroup.setReciverTime(System.currentTimeMillis());
//				// 校验加解密
//			    Object o =channel.getAttribute(AbstractChnnel.IS_VERIFY_DATA);
//			    if(o==null){
//			    	LogSystem.warn("channel,AbstractChnnel.IS_VERIFY_DATA,is null,id = "+channel.getChannelId()+",ProtocolType="+channel.getProtocolType());
//			    	return false;
//			    }
//				boolean isverifyDateChannel = (Boolean)o;
//				if (isverifyDateChannel&&!SecretManager
//						.getInstance()
//						.getIEncrypAndVerifyDatasById(
//								(String) channel
//										.getAttribute(AbstractChnnel.SECRET_ID))
//						.verifyDatas(inputStream)) {
//					LogSystem.warn("加密算法校验失败！"+new Date()+",sessionId="+channel.getChannelId());
//					return false;
//				}
//			msgGroup.decode(inputStream);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			LogSystem.error(e, "解流错误！");
//			isDecodeSucess=false;
//		}finally{
//			try {
//				inputStream.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				LogSystem.error(e, "解流错误！");
//				isDecodeSucess=false;
//			}
//		}
//		if (isDecodeSucess) {
//			List<Msg> msgVector = msgGroup.getMsgsList();
//			this.setMsgVector(msgVector);	
//		} else {
//			channel.close();
//			LogSystem.warn("消息头解析不成功,时间"+new Date()+",date大小"+datas.length+",关闭该连接！");
//		}
//		return isDecodeSucess;
//	}
    /**
     * 解析服务器间交互数据
     */
	public boolean parseByteServer(Channel channel){
		List<Msg> list = DataCodecFactory.getInstance().decodeMsgServer(datas);
		this.setMsgVector(list);
		return true;
	}
	/**
	 * 解析用户请求数据
	 * @param channel
	 * @return
	 */
	public boolean parseByteUser(Channel channel){
		List<Msg> list = DataCodecFactory.getInstance().decodeMsgUser(datas);
		this.setMsgVector(list);
		return true;
	}
}
