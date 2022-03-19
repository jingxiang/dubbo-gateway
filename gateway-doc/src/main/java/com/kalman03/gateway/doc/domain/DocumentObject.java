package com.kalman03.gateway.doc.domain;

import java.util.List;

import lombok.Data;

/**
 * @author kalman03
 * @since 2022-03-19
 */
@Data
public class DocumentObject {
	/**
	 * 应用名称
	 */
	private String projectName;
	/**
	 * 文档更新时间  UTC时间
	 */
	private String createTime;
	/**
	 * 该应用文档的整体描述
	 */
	private String description;
	/**
	 * API服务列表
	 */
	private List<ApiClassInfo> classList;
	
}
