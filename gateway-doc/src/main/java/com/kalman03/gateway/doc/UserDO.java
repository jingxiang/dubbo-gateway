package com.kalman03.gateway.doc;

import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户对象
 * 
 * @author Stranger
 * @since 2022-03-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserDO 
extends SimpleUserDO 
{

	/**
	 * 用户map
	 */
	private Map<String, UserDO> userMap;

}
