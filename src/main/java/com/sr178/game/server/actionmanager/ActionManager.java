package com.sr178.game.server.actionmanager;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import com.sr178.game.framework.log.LogSystem;
import com.sr178.game.msgbody.common.model.Msg;
import com.sr178.game.server.annotation.GameAction;
import com.sr178.game.server.annotation.ISingleThreadRuler;

/**
 * action管理器
 * @author mengchao
 *
 */
public class ActionManager implements BeanPostProcessor{
    private static Map<String,ActionBean>  actionMap = new HashMap<String,ActionBean>();
    //单线程规则map
    private static Map<String,ISingleThreadRuler> singlerThreadActionMethodRulerMap = new HashMap<String,ISingleThreadRuler>();
    
    /**
     * 获取用户action
     * @param actionName
     * @return
     */
    public static ActionBean getAction(String actionName){
    	return actionMap.get(actionName);
    }
    /**
     * 获取所有接口名称
     * @return
     */
    public static List<String> getAllActionName(){
    	 List<String> result = new ArrayList<String>();
    	 for(String key:actionMap.keySet()){
    		 result.add(key);
    	 }
    	 return result;
    }
    
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		GameAction actionAnnotation = bean.getClass().getAnnotation(GameAction.class);
		if(actionAnnotation != null){
			registerActionMethod(bean.getClass(),bean);
		}
		if(bean instanceof ISingleThreadRuler){
			ISingleThreadRuler isingle = (ISingleThreadRuler)bean;
			singlerThreadActionMethodRulerMap.put(isingle.getCmdCode(), isingle);
			LogSystem.info("加载接口="+isingle.getCmdCode()+",单线程运行规则---->>"+isingle.getClass().getSimpleName());
		}
		return bean;
	}
	/**
	 * 注册action开放的方法
	 * @param actionClass
	 */
	private void registerActionMethod(Class<?> actionClass,Object action){
		Method mm[]=actionClass.getDeclaredMethods();
		for(Method m:mm){
			if(!Modifier.isPublic(m.getModifiers())){
				continue;
			}
			Class<?> parameterTypes[]=m.getParameterTypes();
			if(parameterTypes==null||parameterTypes.length!=2 ||!parameterTypes[0].equals(Msg.class)){
				LogSystem.warn("action="+actionClass.getSimpleName()+"方法="+m.getName()+",不是标准的接口定义类型！！！忽略加载！！");
				continue;
			}
			String key=actionClass.getSimpleName()+"_"+m.getName();
			//默认没有写标注的都为开放给用户调用的action 该类型action只做用户是否登录的校验  
			ActionBean actionBean = new ActionBean();
			actionBean.setAction(action);
			actionBean.setMethod(m);
			actionMap.put(key, actionBean);
//			if(m.getAnnotation(SingleThread.class)!=null){
//				singleThreadActionMethod.put(key, null);
//				LogSystem.info("接口="+key+",为全局单线程执行接口！！");
//			}
			LogSystem.info("加载服务器间通讯接口"+key);
		}
	}
	/**
	 * 是否单线程执行方法
	 * @param cmdCode
	 * @return
	 */
	public static ISingleThreadRuler isSingleThread(String cmdCode){
		return singlerThreadActionMethodRulerMap.get(cmdCode);
	}
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}
    
    
}
