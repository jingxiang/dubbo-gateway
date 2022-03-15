package com.kalman03.gateway.constants;

import com.kalman03.gateway.interceptor.AbstractRouteHandlerInterceptor;

/**
 * 路由规则
 * 
 * @author kalman03
 * @since 2022-03-15
 */
public enum RouteRuleType {
	/**
	 * Request url should like this: <b>
	 * http(s)://{domain}/{appName}/{interfaceName}/{method}/{group}/{version} </b>
	 */
	PATH("path"),
	/**
	 * <pre>
	 * Request url should like this:
	 * <b>http(s)://{domain}/{interfaceName}/{method}</b>
	 * 
	 * And request headers should contains: <b>x-app-name,x-group,x-version,x-route-rule</b> .
	 * Set<b> x-route-rule</b> with value<b> mix</b>
	 * @see {@link CoustomHeaderNames}
	 * </pre>
	 */
	MIX("mix"),
	/**
	 * Custom route must declare a sub class extends
	 * <code>com.kalman03.gateway.interceptor.AbstractRouteHandlerInterceptor</code>.
	 * Also,request header contains header <b> x-route-rule</b> with
	 * value<b> custom</b>
	 * 
	 * @see {@link AbstractRouteHandlerInterceptor}
	 */
	CUSTOM("custom");

	private String value;

	private RouteRuleType(String value) {
		this.value = value;
	}

	public static RouteRuleType getType(String value) {
		for (RouteRuleType routeRuleType : RouteRuleType.values()) {
			if (routeRuleType.getValue().equals(value)) {
				return routeRuleType;
			}
		}
		return null;
	}

	public String getValue() {
		return value;
	}
}
