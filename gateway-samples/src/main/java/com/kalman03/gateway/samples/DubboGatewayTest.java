package com.kalman03.gateway.samples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.kalman03.gateway.constants.RouteRuleType;
import com.kalman03.gateway.http.GatewayHttpRequest;
import com.kalman03.gateway.http.GatewayHttpResponse;
import com.kalman03.gateway.interceptor.HandlerInterceptor;
import com.kalman03.gateway.interceptor.InterceptorRule;

/**
 * @author kalman03
 * @since 2022-03-15
 */
@SpringBootApplication
@EnableAutoConfiguration
public class DubboGatewayTest {

	public static void main(String[] args) {
		try {
			SpringApplication.run(DubboGatewayTest.class, args);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Component
	@Order(100)
	@InterceptorRule(routeRuleType = RouteRuleType.PATH, excludePatterns = { "/api/**" })
	class CustomInterceptor implements HandlerInterceptor {
		@Override
		public boolean preHandle(GatewayHttpRequest request, GatewayHttpResponse response) throws Exception {
			System.out.println("preHandle");
			return true;
		}
		@Override
		public void afterCompletion(GatewayHttpRequest request, GatewayHttpResponse response, Exception ex)
				throws Exception {
			System.out.println("afterCompletion");
		}
	}
}
