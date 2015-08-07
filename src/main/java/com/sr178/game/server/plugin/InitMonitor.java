package com.sr178.game.server.plugin;

import com.sr178.game.framework.plugin.ISystemAppPlugin;
import com.sr178.game.server.monitor.MonitorService;

public class InitMonitor implements ISystemAppPlugin{

	@Override
	public void startup() throws Exception {
		MonitorService.getInstance().initMonitor();
	}

	@Override
	public void shutdown() throws Exception {
		
	}

	@Override
	public int spOrder() {
		return 0;
	}

}
