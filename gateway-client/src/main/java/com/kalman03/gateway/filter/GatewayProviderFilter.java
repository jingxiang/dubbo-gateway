package com.kalman03.gateway.filter;

import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;

import com.kalman03.gateway.context.RpcThreadContext;

/**
 * @author kalman03
 * @since 2021-04-29
 */
public class GatewayProviderFilter implements Filter {

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		RpcThreadContext.resetContextMap(RpcContext.getContext().getObjectAttachments());
		return invoker.invoke(invocation);
	}
}