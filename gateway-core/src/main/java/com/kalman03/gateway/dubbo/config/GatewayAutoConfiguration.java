package com.kalman03.gateway.dubbo.config;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.metadata.report.MetadataReport;
import org.apache.dubbo.metadata.report.MetadataReportFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.kalman03.gateway.constants.SystemConstants;

/**
 * @author kalman03
 * @since 2022-03-15
 */
@Configuration(proxyBeanMethods = false)
@ComponentScan({"com.kalman03.gateway"})
public class GatewayAutoConfiguration {

	@Value("${gateway.dubbo.registry.address}")
	private String registryAddress;

	@Bean
	public ApplicationConfig applicationConfig() {
		ApplicationConfig applicationConfig = new ApplicationConfig();
		applicationConfig.setName(SystemConstants.APPLICATION_NAME);
		RegistryConfig registryConfig = new RegistryConfig();
		registryConfig.setAddress(registryAddress);
		applicationConfig.setRegistry(registryConfig);
		return applicationConfig;
	}

	@Bean
	public MetadataReport metadataReport() {
		URL url = URL.valueOf(registryAddress);
		MetadataReportFactory metadataReportFactory = ExtensionLoader.getExtensionLoader(MetadataReportFactory.class)
				.getAdaptiveExtension();
		return metadataReportFactory.getMetadataReport(url);
	}
}
