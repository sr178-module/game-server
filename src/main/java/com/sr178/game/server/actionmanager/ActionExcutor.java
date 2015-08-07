package com.sr178.game.server.actionmanager;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;









import com.codahale.metrics.Timer;
import com.sr178.game.framework.log.LogSystem;
import com.sr178.game.msgbody.common.model.ICodeAble;
import com.sr178.game.msgbody.common.model.Msg;
import com.sr178.game.msgbody.common.model.MsgHead;
import com.sr178.game.server.callback.CallBackListenerManager;
import com.sr178.game.server.channel.Channel;
import com.sr178.game.server.constant.SystemConstant;
import com.sr178.game.server.exception.ServiceException;
import com.sr178.game.server.monitor.MonitorService;
import com.sr178.game.server.msg.MsgBuilder;

public class ActionExcutor {
	private static ActionExcutor actionExcutor = new ActionExcutor();
	
	private ActionExcutor() {
	}
	public static ActionExcutor getInstance() {
		return actionExcutor;
	}
	public List<Msg> excutorAction(List<Msg> msgs, Channel channel) {
		List<Msg> reList = new ArrayList<Msg>();
		if (msgs != null && msgs.size() > 0) {
			for (Msg msg : msgs) {
				Msg response = dealOneMsg(msg,channel);
				if(response!=null){
					reList.add(response);
				}
			}
		}
		return reList;
	}
	
	public Msg excutorAction(Msg msg, Channel channel){
		Timer.Context timer = MonitorService.getInstance().actionExecutorProcessTimer();
		 Msg result = dealOneMsg(msg,channel);
		 if(timer!=null){
			 timer.stop();
		 }
		 return result;
	}
	
	private Msg dealOneMsg(Msg msg,Channel channel){
		ICodeAble response = null;
		MsgHead msgHead = null;
		try {
			// 定义响应消息体
			response = null;
			msgHead = msg.getMsgHead();
			// 响应消息
			if (msgHead.getMsgType() == MsgHead.TYPEOFRESPONSE) {
				try {
					CallBackListenerManager.getInstance().executeCallBack(
							msg, channel);
				} catch (Exception e) {
					LogSystem.error(e,"回调出错");
				}
				return null;
			}
			// 先查询业务逻辑 action
			ActionBean actionBean = ActionManager.getAction(msgHead
					.getCmdCode());
			LogSystem.debug("执行cmdCode = "+msgHead
					.getCmdCode());
			if (actionBean != null) {
				long startTime = System.currentTimeMillis();
					Object res = actionBean.getMethod().invoke(
							actionBean.getAction(), msg, channel);
					if (res != null) {
						response = (ICodeAble) res;
					}
					msgHead.setErrorCode(SystemConstant.SUCCESS_CODE);

				
				long endTime = System.currentTimeMillis();
				LogSystem.debug("接口执行时间,cmdCode="
						+ msgHead.getCmdCode() + "--"
						+ (endTime - startTime) + "/毫秒");
			} else {
				LogSystem.error(new RuntimeException("找不到cmdCode="
						+ msgHead.getCmdCode() + ",action没加入到xml文件中？"),
						"");
			}
		}catch(InvocationTargetException e){
			Throwable t = e.getTargetException();
			if (t instanceof ServiceException) {
				ServiceException se = (ServiceException)t;
				msg.getMsgHead().setErrorCode(se.getCode());
				LogSystem.debug("service异常="+t.getMessage());
			} else if(t instanceof Exception){
				msg.getMsgHead().setErrorCode(SystemConstant.FAIL_CODE);
				LogSystem.error((Exception)t, "执行action发生了异常"+t.getMessage());
			}else{
				msg.getMsgHead().setErrorCode(SystemConstant.FAIL_CODE);
				LogSystem.error(e,"执行action发生了异常"+t.getMessage());
			}
		} 
		catch(ServiceException e){
			msg.getMsgHead().setErrorCode(e.getCode());
			LogSystem.debug("service错误===="+e.getCode()+"--"+e.getMessage());
		}catch (Throwable e) {
			msg.getMsgHead().setErrorCode(SystemConstant.FAIL_CODE);
			LogSystem.error(e, "执行action发生了异常");
		}
		if (response != null
					|| msgHead.getErrorCode() != SystemConstant.SUCCESS_CODE) {
				return MsgBuilder.buildResponseMsg(msg, msgHead.getErrorCode(), response);
	   }
	   return null;
	}
}
