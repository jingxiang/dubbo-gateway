package com.kalman03.gateway.doc.utils;

import java.util.Objects;

import com.thoughtworks.qdox.model.JavaClass;

/**
 * @author kalman03
 * @since 2022-03-19
 */
public class JavaTypeUtils {

	public static boolean isObject(JavaClass javaClass) {
		String className = javaClass.getCanonicalName();
		String simpleName = JavaClassUtils.getClassSimpleName(className);
		return !isPrimitive(simpleName) && !isArray(simpleName) && !isCollection(className) && !isMap(className);
	}

	public static boolean isObjectOrObjectArray(String type0) {
		return "java.lang.Object".equals(type0) || "java.lang.Object[]".equals(type0);
	}

	public static boolean isPrimitive(String type0) {
		if (Objects.isNull(type0)) {
			return true;
		}
		String type = type0.contains("java.lang") ? type0.substring(type0.lastIndexOf(".") + 1, type0.length()) : type0;
		type = type.toLowerCase();
		switch (type) {
		case "integer":
		case "void":
		case "int":
		case "long":
		case "double":
		case "float":
		case "short":
		case "bigdecimal":
		case "char":
		case "string":
		case "number":
		case "boolean":
		case "byte":
		case "uuid":
		case "java.sql.timestamp":
		case "java.util.date":
		case "java.time.localdatetime":
		case "java.time.localtime":
		case "localdatetime":
		case "localdate":
		case "zoneddatetime":
		case "java.time.localdate":
		case "java.time.zoneddatetime":
		case "java.math.bigdecimal":
		case "java.math.biginteger":
		case "java.util.uuid":
		case "java.io.serializable":
			return true;
		default:
			return false;
		}
	}

	public static boolean isCollection(String type) {
		switch (type) {
		case "java.util.List":
		case "java.util.LinkedList":
		case "java.util.ArrayList":
		case "java.util.Set":
		case "java.util.TreeSet":
		case "java.util.HashSet":
		case "java.util.SortedSet":
		case "java.util.Collection":
		case "java.util.ArrayDeque":
		case "java.util.PriorityQueue":
			return true;
		default:
			return false;
		}
	}

	public static boolean isMap(String type) {
		if (type.contains("<")) {
			type = type.substring(0, type.indexOf("<") );
		}
		switch (type) {
		case "java.util.Map":
		case "java.util.SortedMap":
		case "java.util.TreeMap":
		case "java.util.LinkedHashMap":
		case "java.util.HashMap":
		case "java.util.concurrent.ConcurrentHashMap":
		case "java.util.concurrent.ConcurrentMap":
		case "java.util.Properties":
		case "java.util.Hashtable":
			return true;
		default:
			return false;
		}
	}

	public static boolean isArray(String type) {
		return type.endsWith("[]");
	}

	public static boolean isObjectArray(String type) {
		return type.endsWith("Object[]");
	}

	/**
	 * check JSR303
	 *
	 * @param annotationSimpleName annotation name
	 * @return boolean
	 */
	public static boolean isJSR303Required(String annotationSimpleName) {
		switch (annotationSimpleName) {
		case "NotNull":
		case "NotEmpty":
		case "NotBlank":
		case "Required":
			return true;
		default:
			return false;
		}
	}

	public static boolean isRequiredTag(String tagName) {
		return "required".equals(tagName);
	}

	public static boolean isIgnoreTag(String tagName) {
		return "ignore".equals(tagName);
	}

}
