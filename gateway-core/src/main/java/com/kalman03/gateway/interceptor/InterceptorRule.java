package com.kalman03.gateway.interceptor;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.kalman03.gateway.constants.RouteRuleType;

/**
 * @author kalman03
 * @since 2022-03-15
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Inherited
public @interface InterceptorRule {

	String[] includePatterns() default {};

	String[] excludePatterns() default {};

	RouteRuleType[] routeRuleType() default RouteRuleType.PATH;
}
