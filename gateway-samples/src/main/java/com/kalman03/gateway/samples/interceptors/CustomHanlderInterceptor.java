package com.kalman03.gateway.samples.interceptors;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.kalman03.gateway.constants.RouteRuleType;
import com.kalman03.gateway.http.GatewayHttpRequest;
import com.kalman03.gateway.http.GatewayHttpResponse;
import com.kalman03.gateway.interceptor.HandlerInterceptor;
import com.kalman03.gateway.interceptor.InterceptorRule;

/**
 * 
 */
@Component
@Order(100)
@InterceptorRule(routeRuleType = RouteRuleType.PATH, includePatterns = { "/gateway-samples-provider/**" })
public class CustomHanlderInterceptor implements HandlerInterceptor {
	@Override
	public boolean preHandle(GatewayHttpRequest request, GatewayHttpResponse response) throws Exception {
		System.out.println(" CustomHanlderInterceptor preHandle");
		return true;
	}

	@Override
	public void afterCompletion(GatewayHttpRequest request, GatewayHttpResponse response, Exception ex)
			throws Exception {
		System.out.println("CustomHanlderInterceptor afterCompletion");
	}
}