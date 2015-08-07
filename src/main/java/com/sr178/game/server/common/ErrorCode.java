package com.sr178.game.server.common;

/**
 * 错误码封装类
 * 
 * @author mengchao
 * 
 */
public class ErrorCode {

	// 错误码
	private int errorCode;

	// 错误码描述
	private String errorDisc = new String("");

	public ErrorCode(int errorCode, String errorDisc) {
		this.errorCode = errorCode;
		this.errorDisc = errorDisc;
	}
	
	public ErrorCode() {
	}



	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorDisc() {
		return errorDisc;
	}

	public void setErrorDisc(String errorDisc) {
		this.errorDisc = errorDisc;
	}
	
	public String toString()
	{
		StringBuffer tsb=new StringBuffer();
		tsb.append("errorCode:");
		tsb.append(errorCode+"  ");
		tsb.append("errorDisc:");
		tsb.append(errorDisc);
		return tsb.toString();
	}
}
