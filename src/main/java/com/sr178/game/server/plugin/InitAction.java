package com.sr178.game.server.plugin;

import com.sr178.game.framework.plugin.ISystemAppPlugin;
import com.sr178.game.msgbody.server.ReqRegisterActionNameMsgBody;
import com.sr178.game.server.actionmanager.ActionManager;
import com.sr178.game.server.config.LocalTools;
import com.sr178.game.server.msg.MsgDispatchCenter;
import com.sr178.game.server.msg.ServerType;

public class InitAction implements ISystemAppPlugin{

	@Override
	public void shutdown() throws Exception {
		
	}

	@Override
	public void startup() throws Exception {
		ReqRegisterActionNameMsgBody reqRegisterActionNameMsgBody = new ReqRegisterActionNameMsgBody();
		reqRegisterActionNameMsgBody.setServerType(LocalTools.getLocalConfig().getServerType());
		reqRegisterActionNameMsgBody.setActionNameList(ActionManager.getAllActionName()); 
		MsgDispatchCenter.disPatchServerMsg(ServerType.GATEWAY_SERVER, "GateWayAction_registerActionName", reqRegisterActionNameMsgBody);
	}

	@Override
	public int spOrder() {
		return -8000;
	}

}
