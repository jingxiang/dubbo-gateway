package com.kalman03.gateway.interceptor;

import com.kalman03.gateway.constants.AttributeConstans;
import com.kalman03.gateway.dubbo.DubboRoute;
import com.kalman03.gateway.http.GatewayHttpRequest;
import com.kalman03.gateway.http.GatewayHttpResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * @author kalman03
 * @since 2022-03-15
 */
@Slf4j
public abstract class AbstractRouteHandlerInterceptor implements HandlerInterceptor {

	abstract DubboRoute resolvingDubboRoute(GatewayHttpRequest request);

	@Override
	public boolean preHandle(GatewayHttpRequest request, GatewayHttpResponse response) throws Exception {
		DubboRoute dubboRoute = resolvingDubboRoute(request);
		if (dubboRoute == null) {
			log.warn("Route parameters error.Request should matches Path or Mix route rule.");
			response.setRouteError();
			return false;
		}
		request.setAttribute(AttributeConstans.DUBBO_ROUTE, dubboRoute);
		return true;
	}
}
