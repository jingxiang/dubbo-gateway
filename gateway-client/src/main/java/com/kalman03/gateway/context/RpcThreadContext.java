package com.kalman03.gateway.context;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

import com.kalman03.gateway.constants.RpcThreadContextKey;

/**
 * @author kalman03
 * @since 2021-11-20
 */
public class RpcThreadContext {

	private static ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<Map<String, Object>>() {
		@Override
		protected Map<String, Object> initialValue() {
			return newHashMap();
		}
	};

	public static void clear() {
		getContextMap().clear();
	}

	public static void setContextValue(String key, Object value) {
		getContextMap().put(key, value);
	}

	public static Object getContextValue(Object key) {
		return getContextMap().get(key);
	}

	public static void resetContextMap(Map<String, Object> contextMap) {
		threadLocal.get().clear();
		appendContextMap(contextMap);
	}

	public static void appendContextMap(Map<String, Object> contextMap) {
		threadLocal.get().putAll(contextMap);
	}

	public static Map<String, Object> getContextMap() {
		return threadLocal.get();
	}

	@SuppressWarnings("unchecked")
	public static Map<String, String> getRequestHeaders() {
		Object object = getContextValue(RpcThreadContextKey.HEADERS);
		return object != null ? (Map<String, String>) object : newHashMap();
	}

	public static String getRequestId() {
		Object object = getContextValue(RpcThreadContextKey.REQUEST_ID);
		return object != null ? object.toString() : null;
	}
}
