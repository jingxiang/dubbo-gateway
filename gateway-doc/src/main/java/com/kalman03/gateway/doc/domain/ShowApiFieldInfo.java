package com.kalman03.gateway.doc.domain;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * @author kalman03
 * @since 2022-03-19
 */
@Data
@SuperBuilder
public class ShowApiFieldInfo {

	/**
	 * 字段名称
	 */
	private String fieldName;
	/**
	 * 展示字段
	 */
	private String showFieldName;
	/**
	 * 字段类型,eg:String
	 */
	private String fieldType;
	/**
	 * 字段类型，eg:java.lang.String
	 */
	private String fieldFullType;
	/**
	 * 字段描述
	 */
	private String fieldDesc;
	/**
	 * 是否必须字段
	 */
	private boolean required;
	
}
