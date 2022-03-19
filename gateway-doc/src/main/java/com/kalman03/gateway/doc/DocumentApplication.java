package com.kalman03.gateway.doc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.kalman03.gateway.doc.domain.DocumentObject;
import com.kalman03.gateway.doc.domain.ProjectConfig;
import com.kalman03.gateway.doc.service.DocumentService;
import com.kalman03.gateway.doc.service.impl.DefaultDocumentService;

/**
 * @author kalman03
 * @since 2022-03-19
 */
public class DocumentApplication {

	public static void main(String[] args) {
		try {
			ProjectConfig projectConfig = new ProjectConfig();
			projectConfig.setGroup("dubbo");
			projectConfig.setVersion("1.0.0");
			projectConfig.setProjectName("user");
			projectConfig.setDomain("baidu.com");
			DocumentService documentService = new DefaultDocumentService(projectConfig);
			DocumentObject documentObject = documentService.getDocumentObject();
			renderDocument(projectConfig, documentObject);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void renderDocument(ProjectConfig projectConfig, DocumentObject documentObject) throws IOException {
		String projectPath = System.getProperty("user.dir");
		File file = new File(projectPath + "/doc.html");
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		VelocityContext context = new VelocityContext();
		context.put("document", documentObject);
		context.put("project", projectConfig);
		FileWriter writer = new FileWriter(file);
		getVelocityEngine().mergeTemplate("doc.vm", "utf-8", context, writer);
		writer.flush();
		writer.close();
	}

	private static VelocityEngine getVelocityEngine() {
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.addProperty("resource.loader", "class");
		velocityEngine.addProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		return velocityEngine;
	}
}
