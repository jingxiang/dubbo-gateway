package com.kalman03.gateway.doc;

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
public class DocumentApplication {

	public static void main(String[] args) {
		try {
			boolean aliyunOss = false;
			ProjectConfig projectConfig = config(aliyunOss);
			DocumentService documentService = new DefaultDocumentService(projectConfig);
			DocumentObject documentObject = documentService.getDocumentObject();
			AliyunOssProperties ossProperties = projectConfig.getOss();
			OSSClient ossClient = null;
			if(aliyunOss) {
				ossClient(ossProperties);
			}
			DocumentRenderService documentRenderService = new DefaultDocumentRenderService(projectConfig, ossClient);
			documentRenderService.render(documentObject);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static ProjectConfig config(boolean aliyunOss) {
		ProjectConfig projectConfig = new ProjectConfig();
		projectConfig.setGroup("dubbo");
		projectConfig.setVersion("1.0.0");
		projectConfig.setProjectName("Samples");
		projectConfig.setDomain("baidu.com");
		if (aliyunOss) {
			AliyunOssProperties ossProperties = new AliyunOssProperties();
			projectConfig.setOss(ossProperties);
		}
		return projectConfig;
	}

	private static OSSClient ossClient(AliyunOssProperties ossProperties) {
		CredentialsProvider credentialsProvider = new DefaultCredentialProvider(ossProperties.getAccessKeyId(),
				ossProperties.getAccessKeySecret());
		ClientConfiguration config = new ClientConfiguration();
		return new OSSClient(ossProperties.getEndpoint(), credentialsProvider, config);
	}

}
