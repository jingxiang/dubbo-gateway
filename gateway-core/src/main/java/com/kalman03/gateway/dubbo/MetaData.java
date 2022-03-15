package com.kalman03.gateway.dubbo;

import java.io.Serializable;

import org.apache.dubbo.metadata.definition.model.MethodDefinition;
import org.apache.dubbo.metadata.definition.model.ServiceDefinition;
import org.apache.dubbo.metadata.report.identifier.MetadataIdentifier;

import lombok.Data;
/**
 * @author kalman03
 * @since 2022-03-15
 */
@Data
public class MetaData implements Serializable{
    private static final long serialVersionUID = 5162462652546999229L;

	private MetadataIdentifier metadataIdentifier;
	private ServiceDefinition serviceDefinition;
	private MethodDefinition methodDefinition;
}
