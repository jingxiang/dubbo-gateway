package com.kalman03.gateway.doc.service.impl;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ListObjectsV2Request;
import com.aliyun.oss.model.ListObjectsV2Result;
import com.aliyun.oss.model.PutObjectRequest;
import com.kalman03.gateway.doc.domain.AliyunOssProperties;
import com.kalman03.gateway.doc.domain.DocumentObject;
import com.kalman03.gateway.doc.domain.ProjectConfig;
import com.kalman03.gateway.doc.service.DocumentRenderService;

/**
 * @author kalman03
 * @since 2022-03-19
 */
public class DefaultDocumentRenderService implements DocumentRenderService {

	private final ProjectConfig projectConfig;
	private final OSSClient ossClient;
	private final AliyunOssProperties ossProperties;
	private VelocityEngine velocityEngine;

	public DefaultDocumentRenderService(ProjectConfig projectConfig, OSSClient ossClient) {
		this.projectConfig = projectConfig;
		this.ossClient = ossClient;
		this.ossProperties = projectConfig.getOss();

		velocityEngine = new VelocityEngine();
		velocityEngine.addProperty("resource.loader", "class");
		velocityEngine.addProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
	}

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
		if (ossProperties == null) {
			System.out.println("Auto ducuments success.location=" + file.getAbsolutePath());
			System.out.println("You can setting aliyun oss properties if you want to share with others.");
			return;
		}
		boolean needUpdateIndexPage = false;
		List<String> applications = dirAppplications();
		if (CollectionUtils.isEmpty(applications)) {
			applications = newArrayList();
		}
		if (!applications.contains(projectConfig.getProjectName())) {
			needUpdateIndexPage = true;
			applications.add(projectConfig.getProjectName());
		}
		String indexObjectKey = ossProperties.getBucketDir() + "/index.html";
		if (needUpdateIndexPage) {
			File indexFile = renderIndexPage(applications);
			putObject(indexObjectKey, new FileInputStream(indexFile));
			indexFile.delete();
		}

		String docObjectKey = ossProperties.getBucketDir() + "/" + projectConfig.getProjectName() + "/doc.html";
		putObject(docObjectKey, new FileInputStream(file));
		file.delete();
		System.out.println(
				"Auto ducuments success.You can access by link：" + ossProperties.getHost() + "/" + indexObjectKey);
	}

	private void putObject(String objectKey, InputStream content) {
		PutObjectRequest putObjectRequest = new PutObjectRequest(ossProperties.getBucket(), objectKey, content);
		ossClient.putObject(putObjectRequest);
	}

	private List<String> dirAppplications() throws IOException {
		ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request(ossProperties.getBucket());
		listObjectsV2Request.setPrefix(ossProperties.getBucketDir() + "/");
		listObjectsV2Request.setDelimiter("/");
		ListObjectsV2Result result = ossClient.listObjectsV2(listObjectsV2Request);
		List<String> list = new ArrayList<>();
		for (String commonPrefix : result.getCommonPrefixes()) {
			commonPrefix = commonPrefix.replace(ossProperties.getBucketDir() + "/", "");
			if (isBlank(commonPrefix)) {
				continue;
			}
			commonPrefix = commonPrefix.substring(0, commonPrefix.length() - 1);
			list.add(commonPrefix);
		}
		return list;
	}

	private File renderIndexPage(List<String> applications) throws IOException {
		File file = new File("index.html");
		VelocityContext context = new VelocityContext();
		context.put("appList", applications);
		FileWriter writer = new FileWriter(file);
		velocityEngine.mergeTemplate("index.vm", "utf-8", context, writer);
		writer.flush();
		writer.close();
		return file;
	}

	private File renderDocument(DocumentObject documentObject) throws IOException {
		String projectPath = System.getProperty("user.dir");
		String currentProjectPath = projectPath + "/" + projectConfig.getOutPath() + "/doc.html";
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
