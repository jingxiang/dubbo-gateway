package com.kalman03.gateway.service.impl;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.dubbo.common.constants.CommonConstants.PROVIDER_SIDE;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.dubbo.metadata.definition.model.MethodDefinition;
import org.apache.dubbo.metadata.definition.model.ServiceDefinition;
import org.apache.dubbo.metadata.report.identifier.MetadataIdentifier;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.protocol.rest.support.ContentType;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.kalman03.gateway.constants.AttributeConstans;
import com.kalman03.gateway.constants.CoustomHeaderNames;
import com.kalman03.gateway.constants.RouteRuleType;
import com.kalman03.gateway.context.RpcThreadContext;
import com.kalman03.gateway.dubbo.DubboRoute;
import com.kalman03.gateway.dubbo.MetaData;
import com.kalman03.gateway.http.GatewayHttpRequest;
import com.kalman03.gateway.http.GatewayHttpResponse;
import com.kalman03.gateway.interceptor.HandlerInterceptor;
import com.kalman03.gateway.interceptor.HandlerInterceptorAdapter;
import com.kalman03.gateway.interceptor.InterceptorRule;
import com.kalman03.gateway.interceptor.InterceptorMatcher;
import com.kalman03.gateway.service.DubboInvokerService;
import com.kalman03.gateway.service.RequestProxyService;
import com.kalman03.gateway.utils.DubboMatedataUtils;

import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;

/**
 * @author kalman03
 * @since 2022-03-15
 */
@Slf4j
@Component
public class DefaultRequestProxyService implements RequestProxyService, InitializingBean, ApplicationContextAware {
	@Resource
	private DubboInvokerService dubboInvokerService;

	private ApplicationContext applicationContext;
	private List<HandlerInterceptor> handlerInterceptors;

	@Value("${gateway.dubbo.openservice:false}")
	private boolean onlyOpenService;

	@Override
	public void afterPropertiesSet() throws Exception {
		Map<String, HandlerInterceptor> matchingBeans = BeanFactoryUtils
				.beansOfTypeIncludingAncestors(applicationContext, HandlerInterceptor.class, true, false);
		if (!matchingBeans.isEmpty()) {
			this.handlerInterceptors = new ArrayList<>(matchingBeans.values());
			AnnotationAwareOrderComparator.sort(this.handlerInterceptors);
		}
	}

	private List<HandlerInterceptor> getMatchedInterceptors(GatewayHttpRequest request) {
		List<HandlerInterceptor> interceptors = newArrayList();
		if (handlerInterceptors == null || handlerInterceptors.isEmpty()) {
			return interceptors;
		}
		for (HandlerInterceptor handlerInterceptor : handlerInterceptors) {
			InterceptorRule interceptorRule = AnnotationUtils.findAnnotation(handlerInterceptor.getClass(),
					InterceptorRule.class);
			if (interceptorRule == null) {
				interceptors.add(handlerInterceptor);
				continue;
			}
			if (isNotContainsRequestRoute(interceptorRule, request)) {
				continue;
			}
			InterceptorMatcher mappedInterceptor = new InterceptorMatcher(interceptorRule.includePatterns(),
					interceptorRule.excludePatterns(), null);
			if (mappedInterceptor.matches(request)) {
				interceptors.add(handlerInterceptor);
			}
		}
		return interceptors;
	}

	private boolean isNotContainsRequestRoute(InterceptorRule interceptorRule, GatewayHttpRequest request) {
		RouteRuleType reqRuleType = RouteRuleType.getType(
				request.getHeader(CoustomHeaderNames.ROUTE_RULE).orElse(RouteRuleType.PATH.name()).toLowerCase());
		boolean contains = false;
		for (RouteRuleType ruleType : interceptorRule.routeRuleType()) {
			if (ruleType == reqRuleType) {
				contains = true;
				break;
			}
		}
		return !contains;
	}

	@Override
	public void doService(GatewayHttpRequest request, final GatewayHttpResponse response) throws Exception {
		List<HandlerInterceptor> interceptors = getMatchedInterceptors(request);
		HandlerInterceptorAdapter adapter = new HandlerInterceptorAdapter(interceptors);
		Exception exception = null;
		try {
			if (!adapter.preHandle(request, response)) {
				return;
			}
			DubboRoute dubboRoute = request.getAttribute(AttributeConstans.DUBBO_ROUTE, DubboRoute.class);
			if (dubboRoute == null) {
				log.error("Request parameters error.Request should matches Path or Mix or Custom route rule.");
				response.setRouteError();
				return;
			}
			MetadataIdentifier metadataIdentifier = initMetadataIdentifier(dubboRoute);
			ServiceDefinition serviceDefinition = dubboInvokerService.getServiceDefinition(metadataIdentifier);
			if (serviceDefinition == null) {
				log.error("Route service is not register.dubboRoute={}", dubboRoute);
				response.setInternalError("Request service is not register");
				return;
			}
			if (onlyOpenService && !DubboMatedataUtils.isOpenDubboService(serviceDefinition)) {
				log.error("Request service is not register or not declare as an open service, dubboRoute={}",
						dubboRoute);
				response.setInternalError("Request service is not register or not declare as an open service");
				return;
			}
			String body = getBody(request);
			MethodDefinition methodDefinition = DubboMatedataUtils.getMethodDefinition(serviceDefinition,
					dubboRoute.getMethod(), body);
			if (methodDefinition == null) {
				log.error("Request method is not found in service,dubboRoute={}", dubboRoute);
				response.setInternalError("Request is method not found in service");
				return;
			}
			MetaData metaData = new MetaData();
			metaData.setMetadataIdentifier(metadataIdentifier);
			metaData.setServiceDefinition(serviceDefinition);
			metaData.setMethodDefinition(methodDefinition);

			setAttachement();
			
			Object object = dubboInvokerService.invoke(metaData, body);
			response.setResponseBody(object);
			response.setResponseStatus(HttpResponseStatus.OK);
		} catch (Exception e) {
			exception = e;
			response.setInternalServerError();
			throw e;
		} finally {
			adapter.afterCompletion(request, response, exception);
		}
	}
	
	private void setAttachement() {
		for (Map.Entry<String, Object> entry : RpcThreadContext.getContextMap().entrySet()) {
			RpcContext.getContext().setAttachment(entry.getKey(), entry.getValue());
		}
	}

	private String getBody(GatewayHttpRequest request) throws UnsupportedEncodingException {
		try {
			byte[] bodyBytes = ByteBufUtil.getBytes(request.fullHttpRequest().content());
			if (bodyBytes == null || bodyBytes.length <= 0) {
				return null;
			}
			String body = new String(bodyBytes);
			if (request.contentType().orElse("application/json").equals(ContentType.APPLICATION_JSON_UTF_8)) {
				body = form2Payload(body);
			}
			return body;
		} catch (UnsupportedEncodingException e) {
			throw e;
		}
	}

	private static String form2Payload(String body) throws UnsupportedEncodingException {
		Map<String, String> map = newLinkedHashMap();
		String[] array = body.split("&");
		for (String kv : array) {
			String[] pairs = kv.split("=");
			String key = pairs[0];
			if (isNotBlank(pairs[1])) {
				map.put(key, URLDecoder.decode(pairs[1], "utf-8"));
			} else {
				map.put(key, null);
			}
		}
		return JSON.toJSONString(map);
	}

	private MetadataIdentifier initMetadataIdentifier(final DubboRoute dubboRoute) {
		return new MetadataIdentifier(dubboRoute.getInterfaceName(), dubboRoute.getVersion(), dubboRoute.getGroup(),
				PROVIDER_SIDE, dubboRoute.getApplicationName());
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
