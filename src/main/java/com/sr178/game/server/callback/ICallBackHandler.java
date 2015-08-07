package com.sr178.game.server.callback;

import com.sr178.game.msgbody.common.model.Msg;
import com.sr178.game.server.channel.Channel;
/**
 * 服务器间通讯响应回调接口
 * @author magical
 *
 */
public interface ICallBackHandler {
	/**
	 * 成功后执行
	 * @param msg
	 * @param channel
	 */
    public void onSuccess(Msg msg,Channel channel);
    /**
     * 失败后执行
     * @param msg
     * @param channel
     */
    public void OnFail(Msg msg,Channel channel);
}
