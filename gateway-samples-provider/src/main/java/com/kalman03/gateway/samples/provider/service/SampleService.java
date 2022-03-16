package com.kalman03.gateway.samples.provider.service;

import com.kalman03.gateway.annotation.OpenService;
import com.kalman03.gateway.samples.provider.domain.Result;
import com.kalman03.gateway.samples.provider.domain.UserInfo;

/**
 * 这是一个dubbo服务
 * 
 * @author kalman03
 * @since 2022-03-16
 */
@OpenService
public interface SampleService {

	/**
	 * 无参数dubbo服务
	 * 
	 * @return
	 */
	Result<String> getStringResultWithNoParams();

	/**
	 * 演示获取网关透传的参数
	 * 
	 * @return
	 */
	Result<UserInfo> getParamsFromRpcContext();

	/**
	 * 演示对象参数
	 * 
	 * @param userInfo 必须有且仅有一个对象
	 * @return
	 */
	Result<Boolean> saveUser(UserInfo userInfo);
}
