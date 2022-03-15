package com.kalman03.gateway.interceptor;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.kalman03.gateway.http.GatewayHttpRequest;
import com.kalman03.gateway.http.GatewayHttpResponse;

/**
 * @author kalman03
 * @since 2022-03-15
 */
public class HandlerInterceptorAdapter {

	private final List<HandlerInterceptor> interceptorList = new ArrayList<>();

	public HandlerInterceptorAdapter(List<HandlerInterceptor> interceptorList) {
		if (interceptorList != null) {
			this.interceptorList.addAll(interceptorList);
		}
	}

	public boolean preHandle(GatewayHttpRequest request, GatewayHttpResponse response) throws Exception {
		for (HandlerInterceptor interceptor : interceptorList) {
			if (!interceptor.preHandle(request, response)) {
				return false;
			}
		}
		return true;
	}

	public void afterCompletion(GatewayHttpRequest request, GatewayHttpResponse response, @Nullable Exception ex)
			throws Exception {
		for (HandlerInterceptor interceptor : interceptorList) {
			interceptor.afterCompletion(request, response, ex);
		}
	}
}
