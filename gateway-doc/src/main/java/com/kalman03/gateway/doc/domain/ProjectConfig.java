package com.kalman03.gateway.doc.domain;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author kalman03
 * @since 2022-03-19
 */
@ConfigurationProperties(prefix = "gateway.doc.config")
@Data
public class ProjectConfig {
	/**
	 * 启动服务即开始跑
	 */
	private boolean autodoc= true;
	/**
	 * Java源码编码
	 */
	private String charset = "utf-8";
	/**
	 * 网关请求协议
	 */
	private String protocol = "https";
	/**
	 * gateway domain
	 */
	private String domain = "{domain}";
	/**
	 * dubbo application name
	 */
	private String projectName = "{projectName}";
	/**
	 * 该应用文档的整体描述
	 */
	private String description;
	/**
	 * dubbo group
	 */
	private String group = "{group}";
	/**
	 * dubbo version
	 */
	private String version = "{version}";
	/**
	 * 扫描java代码路径
	 */
	private String path = "";
	/**
	 * 文档输出路径（基于当前应用）
	 */
	private String outPath = "/docment";
	/**
	 * 文档要排除的包名
	 */
	private List<String> excludePackages;
	/**
	 * 标志这是一个dubbo服务的annotation，该annotation声明在接口上。如果该参数不配置，标识会扫描所有满足条件的接口服务。
	 */
	private String serviceAnnotation;
	/**
	 * OSS 上传配置
	 */
	private AliyunOssProperties oss;

}
