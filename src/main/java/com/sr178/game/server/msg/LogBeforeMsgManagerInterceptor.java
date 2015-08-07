package com.sr178.game.server.msg;
//package com.easou.game.framework.manager.msg;
//
//import java.lang.reflect.Method;
//import java.util.List;
//
//import org.springframework.aop.MethodBeforeAdvice;
//
//import com.easou.game.framework.log.LogSystem;
//import com.easou.game.msgbody.commom.model.Msg;
//import com.easou.game.msgbody.commom.model.MsgHead;
//
///**
// * 在消息处理之前，log消息头
// * 
// * @author mengchao
// * 
// */
//public class LogBeforeMsgManagerInterceptor implements MethodBeforeAdvice {
//
//	public void before(Method method, Object[] args, Object target)
//			throws Throwable {
//		ByteToMsg byteToMsg = (ByteToMsg) args[1];
//		List<Msg> msgVector = byteToMsg.getMsgVector();
//		Msg clientMsg = msgVector.get(0);
//		MsgHead head = clientMsg.getMsgHead();
//		LogSystem.info("before msg manage, the head is:" + head.toString()+"msg type"+head.getMsgType());
//	}
//
//}
