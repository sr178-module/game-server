package com.sr178.game.server.actionmanager;

import java.lang.reflect.Method;


public class ActionBean {
	private Object action;
	private Method method;
	public Object getAction() {
		return action;
	}
	public void setAction(Object action) {
		this.action = action;
	}
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	
}
