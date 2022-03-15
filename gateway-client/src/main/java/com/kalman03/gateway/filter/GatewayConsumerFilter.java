package com.kalman03.gateway.filter;

import java.util.Map;

import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;

import com.kalman03.gateway.context.RpcThreadContext;

/**
 * @author kalman03
 * @since 2021-11-20
 */
public class GatewayConsumerFilter implements Filter {

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		for (Map.Entry<String, Object> entry : RpcThreadContext.getContextMap().entrySet()) {
			RpcContext.getContext().setAttachment(entry.getKey(), entry.getValue());
		}
		return invoker.invoke(invocation);
	}

}
