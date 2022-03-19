
package com.kalman03.gateway.doc.utils;

import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaParameterizedType;
import com.thoughtworks.qdox.model.JavaType;

/**
 * @author kalman03
 * @since 2022-03-19
 */
public class JavaClassUtils {

	public static String getClassSimpleName(String className) {
		if (className.contains(".")) {
			if (className.contains("<")) {
				className = className.substring(0, className.indexOf("<"));
			}
			int index = className.lastIndexOf(".");
			className = className.substring(index + 1);
		}
		if (className.contains("[")) {
			int index = className.indexOf("[");
			className = className.substring(0, index);
		}
		return className;
	}

	public static String getFiledTypeName(String className) {
		if (className.contains(".")) {
			if (className.contains("<")) {
				className = className.substring(0, className.indexOf("<"));
			}
			int index = className.lastIndexOf(".");
			className = className.substring(index + 1);
		}
		return className;
	}

	public static List<JavaType> getActualTypes(JavaType javaType) {
		if (Objects.isNull(javaType)) {
			return new ArrayList<>(0);
		}
		String typeName = javaType.getGenericFullyQualifiedName();
		if (typeName.contains("<")) {
			return ((JavaParameterizedType) javaType).getActualTypeArguments();
		}
		return new ArrayList<>(0);

	}

	public static String processTypeNameForParams(String javaTypeName) {
		if (isEmpty(javaTypeName)) {
			return "object";
		}
		if (javaTypeName.length() == 1) {
			return "object";
		}
		if (javaTypeName.contains("[]")) {
			return "array";
		}
		switch (javaTypeName) {
		case "java.lang.String":
		case "string":
		case "char":
		case "date":
		case "java.util.UUID":
		case "uuid":
		case "localdatetime":
		case "localdate":
		case "localtime":
		case "timestamp":
		case "zoneddatetime":
		case "java.time.zoneddatetime":
		case "java.time.ZonedDateTime":
			return "string";
		case "java.util.List":
		case "list":
		case "java.util.Set":
		case "set":
		case "java.util.LinkedList":
		case "linkedlist":
		case "java.util.ArrayList":
		case "arraylist":
		case "java.util.TreeSet":
		case "treeset":
			return "array";
		case "java.util.Byte":
		case "byte":
			return "int8";
		case "java.lang.Integer":
		case "integer":
		case "int":
			return "int32";
		case "short":
		case "java.lang.Short":
			return "int16";
		case "double":
			return "double";
		case "java.lang.Long":
		case "long":
			return "int64";
		case "java.lang.Float":
		case "float":
			return "float";
		case "bigdecimal":
		case "biginteger":
			return "number";
		case "java.lang.Boolean":
		case "boolean":
			return "boolean";
		case "map":
			return "map";
		case "multipartfile":
			return "file";
		default:
			return "object";
		}

	}

	public static String getDocletTagValue(JavaClass clazz, String tagName) {
		DocletTag docletTag = clazz.getTagByName(tagName);
		return docletTag == null ? null : docletTag.getValue();
	}

	public static boolean existDocletTag(JavaClass clazz, String tagName) {
		return clazz.getTagByName(tagName) != null;
	}

}
