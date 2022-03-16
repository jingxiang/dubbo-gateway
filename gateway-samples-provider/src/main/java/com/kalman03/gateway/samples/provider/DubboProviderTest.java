package com.kalman03.gateway.samples.provider;

import javax.annotation.PreDestroy;

import org.apache.dubbo.config.DubboShutdownHook;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * @author kalman03
 * @since 2022-03-15
 */
@SpringBootApplication
@ImportResource({ "classpath:dubbo.xml" })
public class DubboProviderTest {

	public static void main(String[] args) {
		try {
			SpringApplication.run(DubboProviderTest.class, args);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@PreDestroy
	public void onClose() {
		System.out.println("start dubbo destroy.....");
		DubboShutdownHook hook = DubboShutdownHook.getDubboShutdownHook();
		hook.doDestroy();
		System.out.println("shutdown dubbo successful");
	}

}
