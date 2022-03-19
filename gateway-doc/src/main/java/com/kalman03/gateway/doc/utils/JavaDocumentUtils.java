package com.kalman03.gateway.doc.utils;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.kalman03.gateway.doc.constants.DocletTagConstants;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaMethod;

/**
 * @author kalman03
 * @since 2022-03-19
 */
public class JavaDocumentUtils {

	public static String getParamCommentsOnMethod(JavaMethod method, String paramName) {
		List<DocletTag> tags = method.getTagsByName(DocletTagConstants.PARAM);
		for (DocletTag docletTag : tags) {
			if (docletTag.getValue().contains(paramName)) {
				return docletTag.getValue().replace(paramName, "").trim();
			}
		}
		return null;
	}

	public static String getReturnCommentsOnMethod(JavaMethod method) {
		DocletTag tag = method.getTagByName(DocletTagConstants.RETURN);
		return tag == null ? null : tag.getValue().trim();
	}

	public static boolean existDocletTag(JavaMethod method, String tagName) {
		return method.getTagByName(tagName) != null;
	}

	public static DocletTag getDocletTag(List<DocletTag> docletTags, String tagName) {
		if (docletTags == null) {
			return null;
		}
		for (DocletTag docletTag : docletTags) {
			if (docletTag.getName().equals(tagName)) {
				return docletTag;
			}
		}
		return null;
	}

	public static boolean existAnnotation(List<JavaAnnotation> annotations, String... annotationNames) {
		if (CollectionUtils.isEmpty(annotations)) {
			return false;
		}
		if (annotationNames == null || annotationNames.length <= 0) {
			return false;
		}
		for (JavaAnnotation annotation : annotations) {
			String name = annotation.getType().getName();
			for (String str : annotationNames) {
				if (name.equals(str)) {
					return true;
				}
			}
		}
		return false;
	}

}
