package com.kalman03.gateway.doc;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

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
import com.kalman03.gateway.doc.service.DocumentService;
import com.kalman03.gateway.doc.service.impl.DefaultDocumentRenderService;
import com.kalman03.gateway.doc.service.impl.DefaultDocumentService;

/**
 * @author kalman03
 * @since 2022-03-19
 */
@Configuration(proxyBeanMethods = false)
public class DocumentAutoConfiguration {
	@Resource
	private ProjectConfig projectConfig;
	@Resource
	private DocumentService documentService;
	@Resource
	private DocumentRenderService documentRenderService;

	@PostConstruct
	public void autoSmartDoc() {
		if (projectConfig.isAutodoc()) {
			new Thread(() -> {
				try {
					long start = System.currentTimeMillis();
					System.out.println("Start to auto documents.");
					DocumentObject documentObject = documentService.getDocumentObject();
					documentRenderService.render(documentObject);
					System.out.println("End auto documents.cost time=" + (System.currentTimeMillis() - start) + "ms");
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

	@Bean(initMethod = "init")
	public DocumentRenderService getDocumentRenderService() {
		AliyunOssProperties ossProperties = projectConfig.getOss();

		OSSClient ossClient = null;
		if (ossProperties != null) {
			CredentialsProvider credentialsProvider = new DefaultCredentialProvider(ossProperties.getAccessKeyId(),
					ossProperties.getAccessKeySecret());
			ClientConfiguration config = new ClientConfiguration();
			ossClient = new OSSClient(ossProperties.getEndpoint(), credentialsProvider, config);
		}
		return new DefaultDocumentRenderService(projectConfig, ossClient);
	}

	@Bean(initMethod = "init")
	public DocumentService getDocumentService() {
		return new DefaultDocumentService(projectConfig);
	}

}
