package com.kalman03.gateway.doc.domain;

import java.util.List;

import com.thoughtworks.qdox.model.JavaType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * @author kalman03
 * @since 2022-03-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class ApiFieldInfo extends ShowApiFieldInfo {

	private transient List<ApiFieldInfo> childFieldList;
	
	private transient List<JavaType> types;

}
