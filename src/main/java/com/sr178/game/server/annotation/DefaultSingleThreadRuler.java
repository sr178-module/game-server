package com.sr178.game.server.annotation;

import com.sr178.game.msgbody.common.model.Msg;
import com.sr178.game.server.util.Utils;

public class DefaultSingleThreadRuler implements ISingleThreadRuler {
	@Override
	public long getRulerKey(Msg msg) {
		return Utils.getUserHashCode(msg.getMsgHead().getFromID());
	}
	@Override
	public String getCmdCode() {
		return "";
	}
}
