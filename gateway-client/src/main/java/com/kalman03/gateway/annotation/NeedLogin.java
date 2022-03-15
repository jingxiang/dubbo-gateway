package com.kalman03.gateway.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 用于声明一个对外的API需要登录才可访问
 * 
 * @author kalman03
 * @since 2021-11-20
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface NeedLogin {

}
