package com.kalman03.gateway.service;

import org.apache.dubbo.metadata.definition.model.ServiceDefinition;
import org.apache.dubbo.metadata.report.identifier.MetadataIdentifier;

import com.kalman03.gateway.dubbo.MetaData;

/**
 * @author kalman03
 * @since 2022-03-15
 */
public interface DubboInvokerService {

	/**
	 * 泛化调用
	 */
    Object invoke(MetaData metadata, String body);

    /**
     * 获取dubbo元数据
     */
    ServiceDefinition getServiceDefinition(MetadataIdentifier metadataIdentifier);
}
