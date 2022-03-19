package com.kalman03.gateway.doc.domain;

import java.util.List;

import lombok.Data;

/**
 * @author kalman03
 * @since 2022-03-19
 */
@Data
public class ApiClassInfo {
	/**
	 * 接口服务名称，即含包路径的类名
	 */
	private String className;
	/**
	 * 创建者
	 */
	private String author;
	/**
	 * 类描述
	 */
	private String description;

	/**
	 * 创建时间
	 */
	private String since;
	/**
	 * 编号
	 */
	private String id;
	/**
	 * 接口是否已注释
	 */
	private boolean deprecated;
	/**
	 * 方法列表
	 */
	private List<ApiMethodInfo> methodList;
}
