package com.sr178.game.server.room;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息发送规则类
 * 
 * @author ws
 * 
 */
public class RestrictionsRule {
	//用户限制类型
	private int type;
	//用户列表
	private Map<String, String> restictionUserMap;
	// 限制规则类型 1 发给房间内指定的用户
	public static final int RULE_TYPE_TO_THIS_PERSON = 1;
	// 2 不发给房间内指定的用户
	public static final int RULE_TYPE_NOT_TO_THIS_PERSON = 2;
	
	/**
	 * @param type
	 */
	public RestrictionsRule(int type) {
		this.type = type;
		restictionUserMap = new HashMap<String, String>();
	}
	public  void addUser(String userId){
		restictionUserMap.put(userId, userId);
	}
	public  void addUser(long userId){
		restictionUserMap.put(userId+"", userId+"");
	}
	
	public void addUserBath(String[] userIds){
		for(String str:userIds){
			addUser(str);
		}
	}
	/**
	 * 判断是否存在用户
	 * @param userId
	 * @return
	 */
	public boolean isExistUser(String userId){
		return restictionUserMap.containsKey(userId);
	}
	/**
	 * 判断某用户是否为受限用户
	 * @param userId
	 * @return
	 */
	public boolean isRestrictionUser(String userId){
		if(type == RULE_TYPE_TO_THIS_PERSON){
			 return !isExistUser(userId);
		}else if(type == RULE_TYPE_NOT_TO_THIS_PERSON){
			return isExistUser(userId);
		}
		return false;
	}
	
	public void addUserBath(long[] userIds){
		for(long str:userIds){
			addUser(str);
		}
	}
	/**
	 * 清理用户
	 */
	public void clearUser(){
		restictionUserMap = new HashMap<String, String>();
	}
	/**
	 * 获取用户数量
	 * @return
	 */
	public int getUserSize(){
		return restictionUserMap.size();
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
