package com.kalman03.gateway.http;

import java.util.Optional;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
/**
 * @author kalman03
 * @since 2022-03-15
 */
public interface GatewayHttpRequest {

	HttpHeaders headers();

	HttpMethod method();

	Optional<String> getHeader(String headerName);

	Optional<String> contentType();

	String path();

	void setAttribute(String key, Object object);

	<T> T getAttribute(String key, Class<T> clazz);
	
	String remoteAddress();
	/**
	 * 获取Query Params，url问号后面的参数
	 */
	String queryParamterValue(String name);
	
	FullHttpRequest fullHttpRequest();
}
