package com.kalman03.gateway.interceptor;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.kalman03.gateway.constants.RouteRuleType;
import com.kalman03.gateway.dubbo.DubboRoute;
import com.kalman03.gateway.http.GatewayHttpRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * @author kalman03
 * @since 2022-03-15
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
@InterceptorRule(routeRuleType = RouteRuleType.PATH)
public class PathRouteHandlerInterceptor extends AbstractRouteHandlerInterceptor {

	// http(s)://{domain}/{appName}/{interfaceName}/{method}/{group}/{version}
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
			dubboRoute.setApplicationName(arrays[0]);
			dubboRoute.setGroup(arrays[3]);
			dubboRoute.setInterfaceName(arrays[1]);
			dubboRoute.setMethod(arrays[2]);
			dubboRoute.setVersion(arrays[4]);
			return dubboRoute;
		} catch (Exception e) {
			log.debug("The request is not compatible with path rule route.path={}", path);
			log.debug("Try to compatible with path error.", e);
		}
		return null;
	}

}
