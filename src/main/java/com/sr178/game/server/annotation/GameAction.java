package com.sr178.game.server.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 服务器对外接口注解  标注了该注解的类都为action类  里面的所有public方法 都对外开放访问  ClassName.MechodName即为方法
 * @author mengchao
 *
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME) 
public @interface GameAction{
}
