package com.sr178.game.server.config;
//本地配置获取工具
public class LocalTools {
	
    private static LocalConfig localConfig;

    private static LocalKeyConfig localKeyConfig;
    /**
     * 获取本地配置
     * @return
     */
	public static LocalConfig getLocalConfig() {
		return localConfig;
	}
	/**
	 * 获取本地密钥配置信息
	 * @return
	 */
	public static LocalKeyConfig getLocalKeyConfig(){
		return localKeyConfig;
	}
	
	public void setLocalConfig(LocalConfig localConfig) {
		LocalTools.localConfig = localConfig;
	}
	
	public void setLocalKeyConfig(LocalKeyConfig localKeyConfig) {
		LocalTools.localKeyConfig = localKeyConfig;
	}
}
