package com.kalman03.gateway.service;

import com.kalman03.gateway.http.GatewayHttpRequest;
import com.kalman03.gateway.http.GatewayHttpResponse;

/**
 * @author kalman03
 * @since 2022-03-15
 */
public interface RequestProxyService {

	void doService(GatewayHttpRequest request, GatewayHttpResponse response) throws Exception;
}
