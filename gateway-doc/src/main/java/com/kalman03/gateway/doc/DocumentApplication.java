package com.kalman03.gateway.doc;

import static com.google.common.collect.Lists.newArrayList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

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
				ossClient = ossClient(ossProperties);
			}
			DocumentRenderService documentRenderService = new DefaultDocumentRenderService(projectConfig, ossClient);
			documentRenderService.render(documentObject);
			
			renderDocument();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void renderDocument() throws IOException {
		String projectPath = System.getProperty("user.dir");
		String currentProjectPath = projectPath + "/index.html";
		File file = new File(currentProjectPath);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		VelocityContext context = new VelocityContext();
		context.put("appList", newArrayList("project1"));
		FileWriter writer = new FileWriter(file);
		VelocityEngine	velocityEngine = new VelocityEngine();
		velocityEngine.addProperty("resource.loader", "class");
		velocityEngine.addProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		velocityEngine.mergeTemplate("index.vm", "utf-8", context, writer);
		writer.flush();
		writer.close();
	}
				
	private static ProjectConfig config(boolean aliyunOss) {
		ProjectConfig projectConfig = new ProjectConfig();
		projectConfig.setGroup("dubbo");
		projectConfig.setVersion("1.0.0");
		projectConfig.setProjectName("Samples");
		projectConfig.setDomain("api.taobao.com");
		projectConfig.setDescription("hello，这是关于Samples的文档");
		if (aliyunOss) {
			AliyunOssProperties ossProperties = new AliyunOssProperties();
			//FIXME
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
