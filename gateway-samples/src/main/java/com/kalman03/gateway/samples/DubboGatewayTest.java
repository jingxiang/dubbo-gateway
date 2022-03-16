package com.kalman03.gateway.samples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
}
