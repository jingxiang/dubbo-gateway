package com.kalman03.gateway.http;

import static com.google.common.collect.Maps.newHashMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;

/**
 * @author kalman03
 * @since 2022-03-15
 */
public class DefaultGatewayHttpRequest implements GatewayHttpRequest {

	private final FullHttpRequest request;
	private final QueryStringDecoder queryStringDecoder;
	private final String remoteAddress;
	private Map<String, Object> attributeMap = newHashMap();

	public DefaultGatewayHttpRequest(FullHttpRequest request, String remoteAddress) {
		this.request = request;
		this.remoteAddress = remoteAddress;
		this.queryStringDecoder = new QueryStringDecoder(request.uri());
	}

	@Override
	public HttpHeaders headers() {
		return request.headers();
	}

	@Override
	public HttpMethod method() {
		return request.method();
	}

	@Override
	public Optional<String> getHeader(String headerName) {
		HttpHeaders headers = headers();
		String value = ObjectUtils.firstNonNull(headers.get(headerName), headers.get(headerName.toLowerCase()));
		return Optional.ofNullable(value);
	}

	@Override
	public Optional<String> contentType() {
		return getHeader(HttpHeaderNames.CONTENT_TYPE.toString());
	}

	@Override
	public String path() {
		return queryStringDecoder.rawPath();
	}

	@Override
	public void setAttribute(String key, Object object) {
		attributeMap.put(key, object);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAttribute(String key, Class<T> clazz) {
		Object object = attributeMap.get(key);
		return object == null ? null : (T) object;
	}

	@Override
	public String remoteAddress() {
		return remoteAddress;
	}

	@Override
	public String queryParamterValue(String name) {
		Map<String, List<String>> parameters = queryStringDecoder.parameters();
		List<String> list = parameters.get(name);
		if (list == null) {
			return null;
		}
		StringBuffer buffer = new StringBuffer();
		for (String str : list) {
			buffer.append(str).append(",");
		}
		String temp = buffer.toString();
		return temp.substring(0, temp.length() - 1);
	}

	@Override
	public FullHttpRequest fullHttpRequest() {
		return request;
	}

}
