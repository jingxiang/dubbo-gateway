package com.kalman03.gateway.doc.domain;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import lombok.Data;

/**
 * @author kalman03
 * @since 2022-03-19
 */
@Data
public class ApiMethodInfo {

	private String id;
	/**
	 * 方法名
	 */
	private String methodName;
	/**
	 * 方法注释
	 */
	private String methodComment;

	private List<ApiFieldInfo> requestParamters;

	private String requestJson;

	private List<ApiFieldInfo> responseParamters;

	private String responseJson;

	/**
	 * 方法是否已注释
	 */
	private boolean deprecated;
	/**
	 * 是否需要登录
	 */
	private boolean needLogin;

	public List<ShowApiFieldInfo> getResponseShowFieldList() {
		List<ShowApiFieldInfo> result = newArrayList();
		if (CollectionUtils.isNotEmpty(responseParamters)) {
			for (ApiFieldInfo fieldInfo : responseParamters) {
				result.addAll(foreach("", fieldInfo));
			}
		}
		return result;
	}

	public List<ShowApiFieldInfo> getRequestShowFieldList() {
		List<ShowApiFieldInfo> result = newArrayList();
		if (CollectionUtils.isNotEmpty(requestParamters)) {
			for (ApiFieldInfo fieldInfo : requestParamters) {
				result.addAll(foreach("", fieldInfo));
			}
		}
		return result;
	}

	private List<ShowApiFieldInfo> foreach(String prefix, ApiFieldInfo fieldInfo) {
		List<ShowApiFieldInfo> result = newArrayList();
		fieldInfo.setShowFieldName(prefix + fieldInfo.getShowFieldName());
		result.add(fieldInfo);
		if (CollectionUtils.isNotEmpty(fieldInfo.getChildFieldList())) {
			if (isBlank(prefix)) {
				prefix = "└─";
			} else {
				prefix = "&nbsp;&nbsp;" + prefix;
			}
			for (ApiFieldInfo item : fieldInfo.getChildFieldList()) {
				result.addAll(foreach(prefix, item));
			}
		}
		return result;
	}
}
