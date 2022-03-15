package com.kalman03.gateway.dubbo;

import lombok.Data;

/**
 * @author kalman03
 * @since 2022-03-15
 */
@Data
public class DubboRoute {

	private String applicationName;
	private String interfaceName;
	private String method;
	private String version;
	private String group;
	
	@Override
	public String toString() {
		return "DubboRoute [applicationName=" + applicationName + ", interfaceName=" + interfaceName + ", method="
				+ method + ", version=" + version + ", group=" + group + "]";
	}

}
