package com.kalman03.gateway.utils;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.List;
import java.util.Map;

import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.metadata.definition.model.MethodDefinition;
import org.apache.dubbo.metadata.definition.model.ServiceDefinition;

import com.kalman03.gateway.annotation.NeedLogin;
import com.kalman03.gateway.annotation.OpenService;

import lombok.Data;
/**
 * @author kalman03
 * @since 2022-03-15
 */
public class DubboMatedataUtils {
	/**
	 * 根据元数据和请求方法名、参数长度获取对应的dubbo服务定义
	 */
	public static MethodDefinition getMethodDefinition(ServiceDefinition serviceDefinition, String methodName, String body) {
		int paramLen = (isBlank(body) || "{}".equals(body)) ? 0 : 1;
		Integer paramLen2 = "{}".equals(body) ? 1 : null;
		if (CollectionUtils.isEmpty(serviceDefinition.getMethods())) {
			return null;
		}
		for (MethodDefinition m : serviceDefinition.getMethods()) {
			if (sameMethod(m, methodName, paramLen)) {
				return m;
			}
		}
		if (paramLen2 != null) {
			for (MethodDefinition m : serviceDefinition.getMethods()) {
				if (sameMethod(m, methodName, paramLen2)) {
					return m;
				}
			}
		}
		return null;
	}

	private static boolean sameMethod(MethodDefinition m, String methodName, int paramLen) {
		return m.getName().equals(methodName) && m.getParameterTypes().length == paramLen;
	}

	/**
	 * 该dubbo接口在网关层面是否需要登录
	 */
	public static boolean isDubboServiceNeedLogin(MethodDefinition methodDefinition, String methodName, int paramLen) {
		List<String> annotations = methodDefinition.getAnnotations();
		if (CollectionUtils.isNotEmpty(annotations)) {
			List<AnnotationData> annotationDatas = getAnnotationData(annotations);
			for (AnnotationData annotationData : annotationDatas) {
				if (annotationData.getClassName().equals(NeedLogin.class.getCanonicalName())) {
					return true;
				}
			}
		}
		return false;
	}

	public static List<AnnotationData> getAnnotationData(List<String> annotations) {
		if (CollectionUtils.isNotEmpty(annotations)) {
			List<AnnotationData> annotationDataList = newArrayList();
			for (String annotationInfo : annotations) {
				if (isBlank(annotationInfo)) {
					continue;
				}
				AnnotationData annotationData = new AnnotationData();
				String[] array = annotationInfo.split("\\(");
				annotationData.setClassName(array[0].substring(1));
				if (array.length <= 1) {
					continue;
				}
				String values = array[1].substring(0, array[1].length() - 1);
				String[] varrays = values.split(",");
				Map<String, Object> fieldMap = newHashMap();
				for (String str : varrays) {
					String[] tempArray = str.split("=");
					if (tempArray.length <= 1) {
						fieldMap.put(tempArray[0], null);
					} else {
						fieldMap.put(tempArray[0], tempArray[1]);
					}
				}
				annotationData.setFieldMap(fieldMap);
				annotationDataList.add(annotationData);
			}
			return annotationDataList;
		}
		return null;
	}

	@Data
	public static class AnnotationData {
		private String className;
		private Map<String, Object> fieldMap;
	}

	/**
	 * 是否是一个开放的dubbo服务
	 */
	public static boolean isOpenDubboService(ServiceDefinition serviceDefinition) {
		if (serviceDefinition == null) {
			return false;
		}
		List<String> annotations = serviceDefinition.getAnnotations();
		if (CollectionUtils.isNotEmpty(annotations)) {
			List<AnnotationData> annotationDatas = getAnnotationData(annotations);
			for (AnnotationData annotationData : annotationDatas) {
				if (annotationData.getClassName().equals(OpenService.class.getCanonicalName())) {
					return true;
				}
			}
		}
		return false;
	}
}