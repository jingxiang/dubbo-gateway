package com.kalman03.gateway.samples.provider.service.impl;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.kalman03.gateway.context.RpcThreadContext;
import com.kalman03.gateway.samples.provider.domain.Result;
import com.kalman03.gateway.samples.provider.domain.UserInfo;
import com.kalman03.gateway.samples.provider.service.SampleService;

/**
 * @author kalman03
 * @since 2022-03-16
 */
@Service("sampleService")
public class SampleServiceImpl implements SampleService {

	@Override
	public Result<String> getStringResultWithNoParams() {
		return Result.ok("I am kalman03");
	}

	@Override
	public Result<UserInfo> getParamsFromRpcContext() {
		//使用RpcThreadContext的前提条件：配置gatewayProviderFilter
		Object value = RpcThreadContext.getContextValue("userinfo");
		if (value != null) {
			System.out.println("获取到透传数据：" + JSON.toJSONString(value));
			return Result.ok(JSON.parseObject(value.toString(), UserInfo.class));
		} else {
			System.out.println("未获取到透传数据");
			return Result.error("未获取到透传数据");
		}
	}

	@Override
	public Result<Boolean> saveUser(UserInfo userInfo) {
		System.out.println("Request params：" + JSON.toJSONString(userInfo));
		return Result.ok(true);
	}
}
