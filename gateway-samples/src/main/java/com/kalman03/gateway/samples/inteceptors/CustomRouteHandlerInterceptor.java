package com.kalman03.gateway.samples.inteceptors;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.kalman03.gateway.constants.RouteRuleType;
import com.kalman03.gateway.dubbo.DubboRoute;
import com.kalman03.gateway.http.GatewayHttpRequest;
import com.kalman03.gateway.interceptor.AbstractRouteHandlerInterceptor;
import com.kalman03.gateway.interceptor.InterceptorRule;

/**
 * 自定义路由samples
 * 
 * @author kalman03
 * @since 2022-03-16
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@InterceptorRule(routeRuleType = RouteRuleType.CUSTOM)
public class CustomRouteHandlerInterceptor extends AbstractRouteHandlerInterceptor {

	/**
	 * request like:
	 * http(s)://{domain}/{method}/{group}/{version}/{appName}/{interfaceName}
	 * headers should contains:
	 * x-route-rule=custom
	 */
	@Override
	public DubboRoute resolvingDubboRoute(GatewayHttpRequest request) {
		String path = request.path();
		if (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		try {
			String[] arrays = path.split("/", 5);
			DubboRoute dubboRoute = new DubboRoute();
			dubboRoute.setMethod(arrays[0]);
			dubboRoute.setGroup(arrays[1]);
			dubboRoute.setVersion(arrays[2]);
			dubboRoute.setApplicationName(arrays[3]);
			dubboRoute.setInterfaceName(arrays[4]);
			return dubboRoute;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
