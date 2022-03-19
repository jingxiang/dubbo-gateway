package com.kalman03.gateway.doc.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.stereotype.Service;

import com.aliyun.oss.OSSClient;
import com.aliyuncs.IAcsClient;
import com.kalman03.gateway.doc.domain.DocumentObject;
import com.kalman03.gateway.doc.domain.ProjectConfig;
import com.kalman03.gateway.doc.service.DocumentRenderService;

/**
 * @author kalman03
 * @since 2022-03-19
 */
@Service
public class DefaultDocumentRenderService implements DocumentRenderService {

	@Resource
	private ProjectConfig projectConfig;
	@Resource
	private OSSClient ossClient;
	@Resource
	private IAcsClient iAcsClient;

	private VelocityEngine velocityEngine;

	@PostConstruct
	public void init() {
		velocityEngine = new VelocityEngine();
		velocityEngine.addProperty("resource.loader", "class");
		velocityEngine.addProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
	}

	@Override
	public void render(DocumentObject documentObject) throws IOException {
		File file = renderDocument(documentObject);
		if (projectConfig.getOss() != null) {

		}
	}

	private File renderDocument(DocumentObject documentObject) throws IOException {
		String projectPath = System.getProperty("user.dir");
		String currentProjectPath = projectPath + "/" + projectConfig.getOutPath() + "/"
				+ projectConfig.getProjectName() + "/doc.html";
		File file = new File(currentProjectPath);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		VelocityContext context = new VelocityContext();
		context.put("document", documentObject);
		context.put("project", projectConfig);
		FileWriter writer = new FileWriter(file);
		velocityEngine.mergeTemplate("doc.vm", "utf-8", context, writer);
		writer.flush();
		writer.close();
		return file;
	}

}
