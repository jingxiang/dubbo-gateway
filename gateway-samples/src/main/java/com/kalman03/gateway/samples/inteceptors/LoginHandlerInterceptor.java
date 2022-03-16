package com.kalman03.gateway.samples.inteceptors;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.Serializable;

import org.springframework.stereotype.Component;

import com.kalman03.gateway.context.RpcThreadContext;
import com.kalman03.gateway.http.GatewayHttpRequest;
import com.kalman03.gateway.http.GatewayHttpResponse;
import com.kalman03.gateway.interceptor.HandlerInterceptor;
import com.kalman03.gateway.samples.constans.Constants;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Data;

/**
 * @author kalman03
 * @since 2022-03-16
 */
@Component
public class LoginHandlerInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(GatewayHttpRequest request, GatewayHttpResponse response) throws Exception {
		String token = request.getHeader("x-auth-token").orElse(null);
		UserInfo userInfo = resolvingUserInfo(token);
		if (userInfo == null) {
			response.setResponseStatus(HttpResponseStatus.FORBIDDEN);
			response.setResponseBody("login required");
			return false;
		}
		RpcThreadContext.setContextValue(Constants.USER_INFO, userInfo);
		return true;
	}

	private UserInfo resolvingUserInfo(String token) {
		if (isBlank(token)) {
			return null;
		}
		// FIXME some case like access redis cache to get the token value.
		return new UserInfo();
	}

	@Data
	static class UserInfo implements Serializable{
		private static final long serialVersionUID = -6194781045872144379L;
		private long userId;
		private Object otherInfo;
	}

}
