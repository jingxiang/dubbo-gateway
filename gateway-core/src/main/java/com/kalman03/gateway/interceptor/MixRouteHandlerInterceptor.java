package com.kalman03.gateway.interceptor;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.kalman03.gateway.constants.CoustomHeaderNames;
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
@InterceptorRule(routeRuleType = RouteRuleType.MIX)
public class MixRouteHandlerInterceptor extends AbstractRouteHandlerInterceptor {

	/**
	 * <pre>
	 * Request url should like this:
	 * <b>http(s)://{domain}/{interfaceName}/{method}</b>
	 * 
	 * And request headers should contains: <b>x-app-name,x-group,x-version</b> 
	 * 
	 * @see {@link CoustomHeaderNames}
	 * </pre>
	 */
	public DubboRoute resolvingDubboRoute(GatewayHttpRequest request) {
		String path = request.path();
		if (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		try {
			DubboRoute dubboRoute = new DubboRoute();
			String[] arrays = path.split("/", 2);
			dubboRoute.setInterfaceName(arrays[0]);
			dubboRoute.setMethod(arrays[1]);
			dubboRoute.setApplicationName(request.getHeader(CoustomHeaderNames.APP_NAME).get());
			dubboRoute.setGroup(request.getHeader(CoustomHeaderNames.GROUP).get());
			dubboRoute.setVersion(request.getHeader(CoustomHeaderNames.VERSION).get());
			return dubboRoute;
		} catch (Exception e) {
			log.info("The request is not compatible with path rule route.path={}", path);
			log.info("ResolvingDubboRoute with path error.", e);
		}
		return null;
	}

}
