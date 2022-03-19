package com.kalman03.gateway.doc;

import javax.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.kalman03.gateway.doc.domain.AliyunOssProperties;
import com.kalman03.gateway.doc.domain.DocumentObject;
import com.kalman03.gateway.doc.domain.ProjectConfig;
import com.kalman03.gateway.doc.service.DocumentRenderService;
import com.kalman03.gateway.doc.service.impl.DefaultDocumentRenderService;
import com.kalman03.gateway.doc.service.impl.DefaultDocumentService;

/**
 * @author kalman03
 * @since 2022-03-19
 */
@Configuration(proxyBeanMethods = false)
public class DocumentAutoConfiguration {

	@PostConstruct
	public void autoSmartDoc(ProjectConfig projectConfig, OSSClient ossClient) {
		if (projectConfig.isAutodoc()) {
			new Thread(() -> {
				try {
					DocumentObject documentObject = new DefaultDocumentService(projectConfig).getDocumentObject();
					DocumentRenderService documentRenderService = new DefaultDocumentRenderService(projectConfig,
							ossClient);
					documentRenderService.render(documentObject);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}).start();
		}
	}

	@Bean
	public ProjectConfig projectConfig() {
		return new ProjectConfig();
	}

	@Bean(destroyMethod = "shutdown")
	@ConditionalOnProperty(prefix = "gateway.doc.config.oss")
	public OSSClient ossClient(ProjectConfig projectConfig) {
		AliyunOssProperties ossProperties = projectConfig.getOss();
		CredentialsProvider credentialsProvider = new DefaultCredentialProvider(ossProperties.getAccessKeyId(),
				ossProperties.getAccessKeySecret());
		ClientConfiguration config = new ClientConfiguration();
		return new OSSClient(ossProperties.getEndpoint(), credentialsProvider, config);
	}
}
