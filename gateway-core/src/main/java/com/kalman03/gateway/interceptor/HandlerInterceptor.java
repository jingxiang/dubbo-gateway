package com.kalman03.gateway.interceptor;

import javax.annotation.Nullable;

import com.kalman03.gateway.http.GatewayHttpRequest;
import com.kalman03.gateway.http.GatewayHttpResponse;

/**
 * @author kalman03
 * @since 2022-03-15
 */
public interface HandlerInterceptor {

	default boolean preHandle(final GatewayHttpRequest request, final GatewayHttpResponse response) throws Exception {
		return true;
	}

	default void afterCompletion(final GatewayHttpRequest request, final GatewayHttpResponse response,
			@Nullable Exception ex) throws Exception {
	}

}