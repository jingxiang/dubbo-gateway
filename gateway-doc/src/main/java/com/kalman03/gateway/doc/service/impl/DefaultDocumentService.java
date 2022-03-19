package com.kalman03.gateway.doc.service.impl;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.collections4.CollectionUtils;

import com.google.gson.internal.bind.util.ISO8601Utils;
import com.kalman03.gateway.doc.constants.AnnotationConstants;
import com.kalman03.gateway.doc.constants.DocletTagConstants;
import com.kalman03.gateway.doc.domain.ApiClassInfo;
import com.kalman03.gateway.doc.domain.ApiFieldInfo;
import com.kalman03.gateway.doc.domain.ApiMethodInfo;
import com.kalman03.gateway.doc.domain.DocumentObject;
import com.kalman03.gateway.doc.domain.ProjectConfig;
import com.kalman03.gateway.doc.service.DocumentService;
import com.kalman03.gateway.doc.service.JavaMethodService;
import com.kalman03.gateway.doc.utils.GsonUtils;
import com.kalman03.gateway.doc.utils.JavaClassUtils;
import com.kalman03.gateway.doc.utils.JavaDocumentUtils;
import com.kalman03.gateway.doc.utils.JavaTypeUtils;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.directorywalker.DirectoryScanner;
import com.thoughtworks.qdox.directorywalker.FileVisitor;
import com.thoughtworks.qdox.directorywalker.SuffixFilter;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 * @author kalman03
 * @since 2022-03-19
 */
public class DefaultDocumentService implements DocumentService {

	private final ProjectConfig projectConfig;
	private final JavaProjectBuilder javaBuilder;
	private final PodamFactory podamFactory;

	public DefaultDocumentService(ProjectConfig projectConfig) throws IOException {
		this.projectConfig = projectConfig;
		this.javaBuilder = loadResources();
		this.podamFactory = new PodamFactoryImpl();
	}

	@Override
	public DocumentObject getDocumentObject() throws IOException {
		DocumentObject documentObject = new DocumentObject();
		documentObject.setProjectName(projectConfig.getProjectName());
		documentObject.setDescription(projectConfig.getDescription());
		documentObject.setClassList(getApiClassInfoList());
		documentObject.setCreateTime(ISO8601Utils.format(new Date(), false, TimeZone.getDefault()));
		return documentObject;
	}

	private List<ApiClassInfo> getApiClassInfoList() {
		List<ApiClassInfo> classList = newArrayList();
		int id = 1;
		for (JavaClass clazz : javaBuilder.getClasses()) {
			if (!clazz.isInterface() || isClassInExcludePackage(clazz) || !isDubboService(clazz)) {
				continue;
			}
			ApiClassInfo apiClassInfo = new ApiClassInfo();
			apiClassInfo.setAuthor(JavaClassUtils.getDocletTagValue(clazz, DocletTagConstants.AUTHOR));
			apiClassInfo.setClassName(clazz.getCanonicalName());
			apiClassInfo.setDeprecated(JavaClassUtils.existDocletTag(clazz, DocletTagConstants.DEPRECATED));
			apiClassInfo.setDescription(clazz.getComment());
			apiClassInfo.setId(id + "");
			apiClassInfo.setSince(JavaClassUtils.getDocletTagValue(clazz, DocletTagConstants.SINCE));
			apiClassInfo.setMethodList(buildMethodList(clazz, id));
			classList.add(apiClassInfo);
			id++;
		}
		return classList;
	}

	private List<ApiMethodInfo> buildMethodList(final JavaClass clazz, int classId) {
		int id = 1;
		List<ApiMethodInfo> methodList = newArrayList();
		List<JavaMethod> methods = clazz.getMethods();
		for (JavaMethod method : methods) {
			if (method.isPrivate()) {
				continue;
			}
			List<JavaAnnotation> annotations = method.getAnnotations();
			boolean deprecated = JavaDocumentUtils.existAnnotation(annotations, AnnotationConstants.DEPRECATED);
			boolean needLogin = JavaDocumentUtils.existAnnotation(annotations, AnnotationConstants.NEED_LOGIN);

			if (!deprecated) {
				deprecated = JavaDocumentUtils.existDocletTag(method, DocletTagConstants.DEPRECATED);
			}
			JavaMethodService methodService = new DefaultJavaMethodService(method, projectConfig, javaBuilder);
			ApiMethodInfo methodInfo = new ApiMethodInfo();
			methodInfo.setId(classId + "." + id);
			methodInfo.setMethodName(method.getName());
			String methodComments = isBlank(method.getComment()) ? method.getName() : method.getComment();
			methodInfo.setMethodComment(methodComments);// TODO should we check method comments required?
			methodInfo.setDeprecated(deprecated);
			methodInfo.setNeedLogin(needLogin);
			List<ApiFieldInfo> requestParamters = methodService.getRequestFields();
			Map<String, Object> requestParamJsonObject = newHashMap();
			for (ApiFieldInfo apiFieldInfo : requestParamters) {
				requestParamJsonObject.put(apiFieldInfo.getFieldName(), reserveObject(apiFieldInfo));
			}
			methodInfo.setRequestParamters(requestParamters);
			methodInfo.setRequestJson(GsonUtils.prettyJson(requestParamJsonObject));

			Map<String, Object> responseParamJsonObject = newHashMap();
			List<ApiFieldInfo> responseParamters = methodService.getResponseFields();
			for (ApiFieldInfo apiFieldInfo : responseParamters) {
				responseParamJsonObject.put(apiFieldInfo.getFieldName(), reserveObject(apiFieldInfo));
			}
			methodInfo.setResponseJson(GsonUtils.prettyJson(responseParamJsonObject));
			methodInfo.setResponseParamters(responseParamters);
			methodList.add(methodInfo);
			id++;
		}
		return methodList;
	}

	private Object reserveObject(ApiFieldInfo apiFieldInfo) {
		if (JavaTypeUtils.isPrimitive(apiFieldInfo.getFieldFullType())
				|| "java.lang.Object".equals(apiFieldInfo.getFieldFullType())) {
			return manufacturePojo(apiFieldInfo.getFieldFullType());
		} else if (JavaTypeUtils.isArray(apiFieldInfo.getFieldFullType())
				|| JavaTypeUtils.isCollection(apiFieldInfo.getFieldFullType())
				|| JavaTypeUtils.isObjectArray(apiFieldInfo.getFieldFullType())) {
			String childType = apiFieldInfo.getChildFieldList().get(0).getFieldFullType();
			Object object = manufacturePojo(childType);
			return newArrayList(object);
		} else if (JavaTypeUtils.isMap(apiFieldInfo.getFieldFullType())) {
//			Type[] types = new Type[2];
//			List<JavaType> javaTypes = apiFieldInfo.getTypes();
//			try {
//				types[0] = Class.forName(javaTypes.get(0).getCanonicalName()).getClass();
//				types[1] = Class.forName(javaTypes.get(1).getCanonicalName()).getClass();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
			return manufacturePojo(apiFieldInfo.getFieldFullType(), String.class, Object.class);
		}
		return manufacturePojo("java.lang.Object");
	}

	private Object manufacturePojo(String fieldFullType, Type... types) {
		try {
			Class<?> clazz = Class.forName(fieldFullType);
			return podamFactory.manufacturePojoWithFullData(clazz, types);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 检测是否是一个Dubbo服务
	 */
	private boolean isDubboService(JavaClass clazz) {
		if (isNotBlank(projectConfig.getServiceAnnotation())) {
			List<JavaAnnotation> classAnnotations = clazz.getAnnotations();
			for (JavaAnnotation annotation : classAnnotations) {
				String name = annotation.getType().getCanonicalName();
				if (projectConfig.getServiceAnnotation().equals(name)) {
					return true;
				}
			}
		}
		return JavaClassUtils.existDocletTag(clazz, DocletTagConstants.DUBBO);
	}

	/**
	 * 排除掉配置需要排除的类
	 */
	private boolean isClassInExcludePackage(JavaClass clazz) {
		if (CollectionUtils.isEmpty(projectConfig.getExcludePackages())) {
			return false;
		}
		String canonicalName = clazz.getCanonicalName();
		for (String packageName : projectConfig.getExcludePackages()) {
			if (packageName.endsWith("*")) {
				packageName = packageName.substring(0, packageName.length() - 1);
			}
			if (canonicalName.contains(packageName)) {
				return true;
			}
		}
		return false;
	}

	private JavaProjectBuilder loadResources() throws IOException {
		JavaProjectBuilder builder = new JavaProjectBuilder();
		builder.setEncoding(projectConfig.getCharset());
		String filepath = new File(projectConfig.getPath()).getCanonicalPath();
		DirectoryScanner scanner = new DirectoryScanner(new File(filepath));
		scanner.addFilter(new SuffixFilterExtend(".java"));
		scanner.scan(new FileVisitor() {
			public void visitFile(File currentFile) {
				try {
					builder.addSource(currentFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		return builder;
	}

	private class SuffixFilterExtend extends SuffixFilter {
		public SuffixFilterExtend(String suffixFilter) {
			super(suffixFilter);
		}

		@Override
		public boolean filter(File file) {
			if (file.getAbsolutePath().indexOf("main") < 0) {
				return false;
			}
			return super.filter(file);
		}
	}
}
