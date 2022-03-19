package com.kalman03.gateway.doc.service;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.kalman03.gateway.doc.constants.DocletTagConstants;
import com.kalman03.gateway.doc.domain.ApiFieldInfo;
import com.kalman03.gateway.doc.utils.JavaClassUtils;
import com.kalman03.gateway.doc.utils.JavaTypeUtils;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;

/**
 * @author kalman03
 * @since 2022-03-19
 */
public interface JavaMethodService {

	List<ApiFieldInfo> getRequestFields();

	List<ApiFieldInfo> getResponseFields();

	default boolean isRequired(JavaClass javaClass) {
		boolean filedRequired = JavaClassUtils.existDocletTag(javaClass, DocletTagConstants.REQUIRED);
		if (!filedRequired) {
			List<JavaAnnotation> annotations = javaClass.getAnnotations();
			if (CollectionUtils.isNotEmpty(annotations)) {
				for (JavaAnnotation annotation : annotations) {
					if (JavaTypeUtils.isJSR303Required(annotation.getType().getSimpleName())) {
						filedRequired = true;
					}
				}
			}
		}
		return filedRequired;
	}

	default List<JavaField> getAllJavaFieldsIncludeSuper(final JavaClass javaClass) {
		List<JavaField> result = newArrayList();
		if (CollectionUtils.isNotEmpty(javaClass.getFields())) {
			result.addAll(javaClass.getFields());
		}
		JavaClass superClass = javaClass.getSuperJavaClass();
		while (superClass != null && !superClass.getSimpleName().equals("Object")) {
			result.addAll(getAllJavaFieldsIncludeSuper(superClass));
			superClass = superClass.getSuperJavaClass();
		}
		return result;
	}
}
