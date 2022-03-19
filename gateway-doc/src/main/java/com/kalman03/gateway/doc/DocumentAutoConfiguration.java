package com.kalman03.gateway.doc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.kalman03.gateway.doc.domain.AliyunOssProperties;
import com.kalman03.gateway.doc.domain.DocumentObject;
import com.kalman03.gateway.doc.domain.ProjectConfig;
import com.kalman03.gateway.doc.service.DocumentService;
import com.kalman03.gateway.doc.service.impl.DefaultDocumentService;

/**
 * @author kalman03
 * @since 2022-03-19
 */
@Configuration(proxyBeanMethods = false)
public class DocumentAutoConfiguration {

	@PostConstruct
	public void autoSmartDoc(ProjectConfig projectConfig) {
		if (projectConfig.isAutodoc()) {
			new Thread(() -> {
				try {
					DocumentService documentService = new DefaultDocumentService(projectConfig);
					DocumentObject documentObject = documentService.getDocumentObject();
//					renderDocument(projectConfig, documentObject);
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
	public OSSClient ossClient(AliyunOssProperties ossProperties) {
		CredentialsProvider credentialsProvider = new DefaultCredentialProvider(ossProperties.getAccessKeyId(),
				ossProperties.getAccessKeySecret());
		ClientConfiguration config = new ClientConfiguration();
		return new OSSClient(ossProperties.getEndpoint(), credentialsProvider, config);
	}

	@Bean
	@ConditionalOnProperty(prefix = "gateway.doc.config.oss")
	public IAcsClient acsClient(AliyunOssProperties ossProperties) {
		IClientProfile profile = DefaultProfile.getProfile(ossProperties.getRegionId(), ossProperties.getAccessKeyId(),
				ossProperties.getAccessKeySecret());
		return new DefaultAcsClient(profile);
	}
}
