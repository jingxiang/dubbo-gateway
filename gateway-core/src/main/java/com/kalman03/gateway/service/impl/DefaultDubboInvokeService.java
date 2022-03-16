package com.kalman03.gateway.service.impl;

import static org.apache.commons.lang3.StringUtils.isBlank;

import javax.annotation.Resource;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.utils.ReferenceConfigCache;
import org.apache.dubbo.metadata.definition.model.FullServiceDefinition;
import org.apache.dubbo.metadata.definition.model.MethodDefinition;
import org.apache.dubbo.metadata.definition.model.ServiceDefinition;
import org.apache.dubbo.metadata.report.MetadataReport;
import org.apache.dubbo.metadata.report.identifier.MetadataIdentifier;
import org.apache.dubbo.rpc.service.GenericService;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.kalman03.gateway.dubbo.MetaData;
import com.kalman03.gateway.service.DubboInvokerService;
import com.kalman03.gateway.utils.DubboMatedataUtils;

/**
 * @author kalman03
 * @since 2022-03-15
 */
@Service
public class DefaultDubboInvokeService implements DubboInvokerService {

	@Resource
	private ApplicationConfig applicationConfig;
	@Resource
	private MetadataReport metadataReport;

	@Override
	public Object invoke(MetaData metadata, String body) {
		String methodName = metadata.getMethodDefinition().getName();
		Object[] params = bodyToArray(body);
		MethodDefinition methodDefinition = DubboMatedataUtils.getMethodDefinition(metadata.getServiceDefinition(),
				methodName, body);
		String[] paramTypes = methodDefinition.getParameterTypes();
		MetadataIdentifier metadataIdentifier = metadata.getMetadataIdentifier();
		GenericService svc = getReferenceService(metadataIdentifier.getServiceInterface(),
				metadataIdentifier.getGroup(), metadataIdentifier.getVersion());
		return svc.$invoke(methodName, paramTypes, (paramTypes == null || paramTypes.length == 0) ? null : params);
	}

	private GenericService getReferenceService(String interfaceName, String group, String version) {
		ReferenceConfig<GenericService> reference = initReference(interfaceName, group, version);
		return ReferenceConfigCache.getCache().get(reference);
	}

	@SuppressWarnings("deprecation")
	private ReferenceConfig<GenericService> initReference(String interfaceName, String group, String version) {
		ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
		reference.setGeneric(true);
		reference.setApplication(applicationConfig);
		reference.setGroup(group);
		reference.setVersion(version);
		reference.setInterface(interfaceName);
		return reference;
	}

	@Override
	public ServiceDefinition getServiceDefinition(MetadataIdentifier metadataIdentifier) {
		String serviceDefinition = metadataReport.getServiceDefinition(metadataIdentifier);
		return JSON.parseObject(serviceDefinition, FullServiceDefinition.class);
	}

	private static Object[] bodyToArray(String body) {
		if (isBlank(body)) {
			return null;
		}
		return new Object[] { JSON.parseObject(body) };
	}
}
