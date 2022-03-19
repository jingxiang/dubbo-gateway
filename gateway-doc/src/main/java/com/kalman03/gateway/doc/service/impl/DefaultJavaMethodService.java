package com.kalman03.gateway.doc.service.impl;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;

import com.kalman03.gateway.doc.domain.ApiFieldInfo;
import com.kalman03.gateway.doc.domain.ProjectConfig;
import com.kalman03.gateway.doc.service.JavaMethodService;
import com.kalman03.gateway.doc.utils.JavaClassUtils;
import com.kalman03.gateway.doc.utils.JavaDocumentUtils;
import com.kalman03.gateway.doc.utils.JavaTypeUtils;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.model.impl.DefaultJavaParameterizedType;

/**
 * @author kalman03
 * @since 2022-03-19
 */
public class DefaultJavaMethodService implements JavaMethodService {

	private final JavaMethod javaMethod;
	private final ProjectConfig apiConfig;
	private final JavaProjectBuilder javaBuilder;

	public DefaultJavaMethodService(JavaMethod javaMethod, ProjectConfig apiConfig, JavaProjectBuilder javaBuilder) {
		this.javaMethod = javaMethod;
		this.apiConfig = apiConfig;
		this.javaBuilder = javaBuilder;
	}

	@Override
	public List<ApiFieldInfo> getRequestFields() {
		List<JavaParameter> params = javaMethod.getParameters();
		if (params.isEmpty()) {
			return newArrayList();
		}
		int level = 1;
		Set<String> registryClasses = newHashSet();
		List<ApiFieldInfo> fieldList = newArrayList();
		for (JavaParameter param : params) {
			String fieldName = param.getName();
			JavaClass javaClass = param.getJavaClass();
			String canonicalName = javaClass.getCanonicalName();
			String simpleName = JavaClassUtils.getClassSimpleName(canonicalName);
			String fieldDesc = JavaDocumentUtils.getParamCommentsOnMethod(javaMethod, param.getName());
			String fieldType = JavaClassUtils.getFiledTypeName(canonicalName);
			fieldType = JavaClassUtils.processTypeNameForParams(fieldType.toLowerCase());
			boolean filedRequired = isRequired(javaClass);
			if (JavaTypeUtils.isPrimitive(simpleName) || JavaTypeUtils.isObjectOrObjectArray(canonicalName)
					|| JavaTypeUtils.isMap(canonicalName)) {
				ApiFieldInfo apiFieldInfo = ApiFieldInfo.builder().fieldName(fieldName).showFieldName(fieldName)
						.fieldType(fieldType).fieldFullType(canonicalName).fieldDesc(fieldDesc).required(filedRequired)
						.build();
				fieldList.add(apiFieldInfo);
			} else {
				fieldList.addAll(buildChildList(javaClass, fieldName, simpleName, canonicalName, fieldDesc,
						registryClasses, level));
			}
		}
		return fieldList;
	}

	@Override
	public List<ApiFieldInfo> getResponseFields() {
		List<ApiFieldInfo> fieldList = newArrayList();
		JavaClass javaClass = javaMethod.getReturns();
		if (javaClass.isVoid()) {
			return fieldList;
		}
		int level = 1;
		Set<String> registryClasses = newHashSet();
		String fieldName = "object";
		String className = javaClass.getCanonicalName();
		String simpleName = JavaClassUtils.getClassSimpleName(className);
		String fieldDesc = JavaDocumentUtils.getReturnCommentsOnMethod(javaMethod);
		String fieldType = JavaClassUtils.getFiledTypeName(className);
		fieldType = JavaClassUtils.processTypeNameForParams(fieldType.toLowerCase());
		boolean filedRequired = isRequired(javaClass);
		if (JavaTypeUtils.isObjectOrObjectArray(className) || JavaTypeUtils.isPrimitive(simpleName)
				|| JavaTypeUtils.isMap(className)) {
			fieldType = JavaClassUtils.processTypeNameForParams(fieldType.toLowerCase());
			ApiFieldInfo param = ApiFieldInfo.builder().fieldName(fieldName).showFieldName(fieldName)
					.fieldType(fieldType).fieldDesc(fieldDesc).required(filedRequired).build();
			fieldList.add(param);
		} else if (JavaTypeUtils.isArray(className) || JavaTypeUtils.isCollection(className)) {
			String name = ((DefaultJavaParameterizedType) javaClass).getActualTypeArguments().get(0).getCanonicalName();
			fieldList.addAll(buildChildList(javaBuilder.getClassByName(name), fieldName, fieldType, null, null,
					registryClasses, level));
		} else {
			List<JavaField> javaFields = getAllJavaFieldsIncludeSuper(javaClass);
			for (JavaField javaField : javaFields) {
				List<ApiFieldInfo> list = buildChildList(javaField.getType(), javaField.getName(), null, null,
						javaField.getComment(), registryClasses, 1);
				fieldList.addAll(list);
			}
		}
		return fieldList;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<ApiFieldInfo> buildChildList(JavaClass javaClass, String fieldName, String fieldType,
			String fieldFullType, String fieldDesc, Set<String> registryClasses, int level) {
		String className = javaClass.getCanonicalName();
		List<ApiFieldInfo> paramList = new ArrayList<>();
		if (registryClasses.contains(className) && JavaTypeUtils.isObject(javaClass)) {
			return paramList;
		}
		registryClasses.add(className);
		boolean filedRequired = isRequired(javaClass);
		String simpleName = JavaClassUtils.getClassSimpleName(className);
		if (isBlank(fieldType)) {
			fieldType = JavaClassUtils.getFiledTypeName(className);
			fieldFullType = javaClass.getCanonicalName();
		}
		if (JavaTypeUtils.isObjectOrObjectArray(className)) {
			ApiFieldInfo param = ApiFieldInfo.builder().fieldName(fieldName).showFieldName(fieldName)
					.fieldType(fieldType).fieldFullType(fieldFullType).fieldDesc(fieldDesc).required(filedRequired)
					.build();
			paramList.add(param);
		} else if (JavaTypeUtils.isPrimitive(simpleName)) {
			fieldType = JavaClassUtils.processTypeNameForParams(fieldType.toLowerCase());
			ApiFieldInfo param = ApiFieldInfo.builder().fieldName(fieldName).showFieldName(fieldName)
					.fieldType(fieldType).fieldFullType(fieldFullType).fieldDesc(fieldDesc).required(filedRequired)
					.build();
			paramList.add(param);
		} else if (JavaTypeUtils.isArray(className)) {
			String name = ((DefaultJavaParameterizedType) javaClass).getActualTypeArguments().get(0).getCanonicalName();
			paramList.addAll(buildChildList(javaBuilder.getClassByName(name), fieldName, fieldType, fieldFullType,
					fieldDesc, registryClasses, ++level));
		} else if (JavaTypeUtils.isCollection(className)) {
			String name = ((DefaultJavaParameterizedType) javaClass).getActualTypeArguments().get(0).getCanonicalName();
			paramList.addAll(buildChildList(javaBuilder.getClassByName(name), fieldName, fieldType, fieldFullType,
					fieldDesc, registryClasses, ++level));
		} else if (JavaTypeUtils.isMap(className)) {
			List<JavaType> types = ((DefaultJavaParameterizedType) javaClass).getActualTypeArguments();
			ApiFieldInfo param = ApiFieldInfo.builder().fieldName(fieldName).showFieldName(fieldName)
					.fieldType(fieldType).fieldFullType(fieldFullType).fieldDesc(fieldDesc).required(filedRequired)
					.types(types).build();
			paramList.add(param);
		} else {
			if (level == 1) {
				List<ApiFieldInfo> flist = newArrayList();
				List<JavaField> javaFields = getAllJavaFieldsIncludeSuper(javaClass);
				for (JavaField javaField : javaFields) {
					List<ApiFieldInfo> list = buildChildList(javaField.getType(), javaField.getName(), null, null,
							javaField.getComment(), registryClasses, ++level);
					flist.addAll(list);
				}
				if (CollectionUtils.isNotEmpty(flist)) {
					paramList.addAll(flist);
				}
			} else {
				ApiFieldInfo.ApiFieldInfoBuilder builder = ApiFieldInfo.builder();
				builder.fieldName(fieldName).showFieldName(fieldName).fieldType(fieldType).fieldFullType(fieldFullType)
						.fieldDesc(fieldDesc).required(filedRequired);
				List<ApiFieldInfo> childFieldList = newArrayList();

				List<JavaField> javaFields = getAllJavaFieldsIncludeSuper(javaClass);
				int a = ++level;
				for (JavaField javaField : javaFields) {
					List<ApiFieldInfo> list = buildChildList(javaField.getType(), javaField.getName(), null, null,
							javaField.getComment(), registryClasses, a);
					childFieldList.addAll(list);
				}
				if (CollectionUtils.isNotEmpty(childFieldList)) {
					builder.childFieldList(childFieldList);
				}
				paramList.add(builder.build());
			}
		}
		return paramList;

	}
}
