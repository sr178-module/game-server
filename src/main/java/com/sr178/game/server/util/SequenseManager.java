package com.sr178.game.server.util;

import java.util.UUID;

import com.sr178.game.server.concurrent.PaddedAtomicLong;

/**
 * 序列好生成器械
 * 
 * @author mengc
 */
public class SequenseManager {
	// 单例对象
	private static SequenseManager sequenseManager= new SequenseManager();;
	private PaddedAtomicLong  startSeq ;
	// 单例实现 私有构造方法
	private SequenseManager() {
		startSeq = new PaddedAtomicLong(System.nanoTime());
	}

	// 单例实例取得方法
	public static SequenseManager getInstance() {
		return sequenseManager;
	}

	public long generateStaticseq() {
		return startSeq.incrementAndGet();
	}
	/**
	 * 创建一个用户id
	 * 
	 * @return
	 */
	public String getUserId() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	public static void main(String[] args) {
		System.out.println(SequenseManager.getInstance().generateStaticseq());
		System.out.println(SequenseManager.getInstance().generateStaticseq());
	}
}
