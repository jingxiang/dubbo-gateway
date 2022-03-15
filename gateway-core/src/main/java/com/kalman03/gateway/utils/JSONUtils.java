package com.kalman03.gateway.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;

/**
 * @author kalman03
 * @since 2022-03-15
 */
public class JSONUtils {

	public static String filterJsonResponseBody(Object object) {
		SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
		// 排除字段名为class的字段
		filter.getExcludes().add("class");
		return JSON.toJSONString(object, filter, SerializerFeature.DisableCircularReferenceDetect);
	}
}
