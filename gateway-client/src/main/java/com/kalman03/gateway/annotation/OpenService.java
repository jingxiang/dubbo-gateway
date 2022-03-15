package com.kalman03.gateway.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 将dubbo服务暴露为一个网关可直接访问的服务
 * 
 * @author kalman03
 * @since 2021-11-20
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface OpenService {

}
