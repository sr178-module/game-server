package com.sr178.game.server.annotation;

import com.sr178.game.msgbody.common.model.Msg;
/**
 * 单线程规则
 * @author Administrator
 *
 */
public interface ISingleThreadRuler {
	  public String getCmdCode();
      public long getRulerKey(Msg msg);
}
