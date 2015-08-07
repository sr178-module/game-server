package com.sr178.game.server.msg;
//package com.easou.game.framework.manager.msg;
//
//import java.io.IOException;
//import java.util.List;
//
//import com.easou.game.framework.constant.SystemConstant;
//import com.easou.game.framework.log.LogSystem;
//import com.easou.game.framework.manager.user.Session;
//import com.easou.game.framework.manager.user.SessionManager;
//import com.easou.game.framework.server.bridge.BridgeExit;
//import com.easou.game.framework.server.channle.Channel;
//import com.easou.game.framework.stata.TaskStataManager;
//import com.easou.game.framework.task.TaskExcutor;
//import com.easou.game.msgbody.commom.model.Msg;
//import com.easou.game.msgbody.commom.tool.MsgManageTool;
//
//
//
///**
// * 消息处理
// * 
// * @author mengchao
// * 
// */
//public class MsgManagerImp implements MsgManager {
//
//	private BridgeExit bridgeExit = null;
//
//	public MsgManagerImp() {
//
//	}
//
//	public MsgManagerImp(BridgeExit bridgeExit) {
//		this.bridgeExit = bridgeExit;
//	}
//
//	public void msgManage(Channel channel, ByteToMsg byteToMsg) {
//		//解流 放到了外面
////		boolean isSuccece = byteToMsg.parseByte(channel);
//		
//		
//		List<Msg> msgVector = byteToMsg.getMsgVector();
//
//		byte[] resDatas = null;
//		List<Msg> resultList = TaskExcutor.getInstance().excutorTask(msgVector,channel);
//		
//       if(channel.getProtocolType()==SystemConstant.SOCKET_CONNECT){//如果是socket连接
//			try {
//				resDatas = MsgManageTool.saveResponseMsgs(resultList);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				LogSystem.error(e,"");
//			}
//		}else if(channel.getProtocolType()==SystemConstant.HTTP_CONNECT){//如果是http连接
//			String userSequece = msgVector.get(0).getMsgHead().getUserSequense();
//			Session session = SessionManager.getInstance().getSession(userSequece);
//			try {
//				if (session != null) {
//					//如果session不为空 则返回所有消息列表中的数据
//					resDatas = session.getResponseDate(resultList, channel
//							.getClientType());
//				} else{
//					resDatas = MsgManageTool.saveResponseMsgs(resultList);
//				}
//			} catch (IOException e) {
//				LogSystem.error(e,"");
//			}
//		}else{
//			throw new NullPointerException("没有设置连接类型，支持socket和http两种连接");
//		}
//        if(resultList!=null&&resultList.size()>0&&resDatas!=null){
//        	TaskStataManager.getInstance().increaseCmdCodeBytes(resultList.get(0).getMsgHead().getCmdCode(), resDatas.length);
//// 		    LogSystem.info("响应消息数量"+resultList.size()+"---msgHead cmdCode"+resultList.get(0).getMsgHead().getCmdCode()+",发送消息大小"+resDatas.length);
//        }
//		//发送响应数据
//		bridgeExit.sendData(channel, resDatas, false);
//	}
//
//	public BridgeExit getBridgeExit() {
//		return bridgeExit;
//	}
//
//	public void setBridgeExit(BridgeExit bridgeExit) {
//		this.bridgeExit = bridgeExit;
//	}
//}
