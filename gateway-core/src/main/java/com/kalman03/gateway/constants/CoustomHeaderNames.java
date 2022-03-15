package com.kalman03.gateway.constants;

/**
 * @author kalman03
 * @since 2022-03-15
 */
public class CoustomHeaderNames {

	/**
	 * dubbo applicationName
	 */
	public final static String APP_NAME = "x-app-name";
	/**
	 * dubbo group
	 */
	public final static String GROUP = "x-group";
	/**
	 * dubbo version
	 */
	public final static String VERSION = "x-version";
	/**
	 * path or mix ,default is path if the value is blank.
	 * 
	 * @see {@link RouteRuleType}
	 */
	public final static String ROUTE_RULE = "x-route-rule";

}
