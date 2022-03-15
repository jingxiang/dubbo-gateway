package com.kalman03.gateway.http;

import java.util.Map;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Data;

/**
 * @author kalman03
 * @since 2022-03-15
 */
@Data
public class GatewayHttpResponse {

	private HttpResponseStatus responseStatus = HttpResponseStatus.OK;

	private Object responseBody;

	private Map<String, String> headerMap;

	private String contentType = "application/json;charset=UTF-8";

	public void setInternalServerError() {
		setInternalError("Internal server error.");
	}

	public void setRouteError() {
		setInternalError("Route parameters error.Request should matches Path or Mix or Custom route rule.");
	}

	public void setInternalError(String body) {
		setResponseStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
		setResponseBody(body);
	}
}
