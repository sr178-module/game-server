package com.sr178.game.server.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import com.sr178.game.framework.log.LogSystem;


public class MD5 {

	/**
	 * 16位
	 * 
	 * @param plainText
	 * @return
	 */
	public static String md5Of16(String plainText) {
		String result = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			return buf.toString().substring(8, 24);
		} catch (NoSuchAlgorithmException e) {
			LogSystem.error(e, "");
		}
		return result;
	}

	/**
	 * 32位
	 * 
	 * @param plainText
	 * @return
	 */
	public static String md5Of32(String plainText) {
		String result = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			return buf.toString();
		} catch (NoSuchAlgorithmException e) {
			LogSystem.error(e, "");
		}
		return result;
	}
	
	public static void main(String[] args) {
		Date date = new Date();
		System.out.println(MD5.md5Of32("刀剑幻想ios"+date.getTime()).toUpperCase());
	}
}
