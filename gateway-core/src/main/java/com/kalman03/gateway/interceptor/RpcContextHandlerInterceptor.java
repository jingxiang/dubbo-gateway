package com.kalman03.gateway.interceptor;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.kalman03.gateway.constants.RpcThreadContextKey;
import com.kalman03.gateway.context.RpcThreadContext;
import com.kalman03.gateway.http.GatewayHttpRequest;
import com.kalman03.gateway.http.GatewayHttpResponse;

/**
 * @author kalman03
 * @since 2022-03-15
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class RpcContextHandlerInterceptor implements HandlerInterceptor {

	@Value("gateway.rpc.context.headers.allowlist:")
	private String allowHeaders;

	@Override
	public boolean preHandle(GatewayHttpRequest request, GatewayHttpResponse response) throws Exception {
		Map<String, String> headerMap = getAllowHeaderMap(request);
		RpcThreadContext.setContextValue(RpcThreadContextKey.HEADERS, headerMap);
		RpcThreadContext.setContextValue(RpcThreadContextKey.REMOTE_ADDRESS, request.remoteAddress());
		RpcThreadContext.setContextValue(RpcThreadContextKey.REQUEST_ID, UUID.randomUUID().toString());
		return true;
	}

	private Map<String, String> getAllowHeaderMap(GatewayHttpRequest request) {
		Map<String, String> map = getAllHeaders(request);
		Set<String> allowKeys = getAllowHeadersKey();
		if (CollectionUtils.isEmpty(allowKeys)) {
			return map;
		}
		Map<String, String> resultMap = newHashMap();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			if (allowKeys.contains(entry.getKey())) {
				resultMap.put(entry.getKey(), entry.getValue());
			}
		}
		return resultMap;
	}

	private Set<String> getAllowHeadersKey() {
		if (isBlank(allowHeaders)) {
			return newHashSet();
		}
		return StringUtils.commaDelimitedListToSet(",");
	}

	private Map<String, String> getAllHeaders(GatewayHttpRequest request) {
		Map<String, String> map = newHashMap();
		Set<String> names = request.headers().names();
		for (String name : names) {
			map.put(name, request.getHeader(name).orElse(null));
		}
		return map;
	}
}
