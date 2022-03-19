package com.kalman03.gateway.doc;

import java.util.List;
import java.util.Map;

/**
 * 用户服务
 * 
 * @author Stranger
 * @since 2022-03-10
 * @dubbo 111
 */
public interface UserService2 {
	/**
	 * 获取UserDO
	 * 
	 * @param UserDO 用户userDO
	 * @return
	 */
//	UserDO getUserInfo(Object [] objecta);
////
	/**
	 * 获取getSimpleUser
	 * 
	 * @param user aaaaa
	 * @return
	 */
	SimpleUserDO getSimpleUser(UserDO user);
//////
//////	
	List<SimpleUserDO> getSimpleUser2();
	/**
	 * 
	 * @param simpleUserDO 这是啥撒啊啊啊
	 * @return
	 */
	void getSimpleUser3(UserDO simpleUserDO);
	
//	Object getSimpleUser4();
}
