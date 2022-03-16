package com.kalman03.gateway.netty.handler;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.kalman03.gateway.http.DefaultGatewayHttpRequest;
import com.kalman03.gateway.http.GatewayHttpRequest;
import com.kalman03.gateway.http.GatewayHttpResponse;
import com.kalman03.gateway.service.RequestProxyService;
import com.kalman03.gateway.utils.JSONUtils;
import com.kalman03.gateway.utils.NamingThreadFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author kalman03
 * @since 2022-03-15
 */
@ChannelHandler.Sharable
@Slf4j
@Component
public class HttpProcessChannelHandler extends ChannelInboundHandlerAdapter {

	@Resource
	private RequestProxyService requestProxyService;

	@Value("${gateway.netty.business.thread-count:50}")
	private int businessThreadCount;

	private ExecutorService businessThreadPool;

	@PostConstruct
	public void init() {
		businessThreadPool = Executors.newFixedThreadPool(businessThreadCount,new NamingThreadFactory("Gateway-Http-Processer"));
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		businessThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;
				String remoteAddress = getRemoteAddress(fullHttpRequest, ctx);
				GatewayHttpRequest request = new DefaultGatewayHttpRequest(fullHttpRequest, remoteAddress);
				GatewayHttpResponse response = new GatewayHttpResponse();
				if (HttpMethod.OPTIONS.equals(request.method())) {
					response.setResponseBody("");
					flushResponse(ctx, request, response);
					return;
				}
				try {
					requestProxyService.doService(request, response);
					flushResponse(ctx, request, response);
				} catch (Exception ex) {
					log.error("", ex);
					flushResponse(ctx, request, response);
				} finally {
					ReferenceCountUtil.release(msg);
				}
			}
		});
	}
	
	private String getRemoteAddress(FullHttpRequest fullHttpRequest, ChannelHandlerContext ctx) {
		String clientIP = fullHttpRequest.headers().get("x-forwarded-for");
		if (isBlank(clientIP)) {
			InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
			if (insocket != null && insocket.getAddress() != null) {
				clientIP = insocket.getAddress().getHostAddress();
			}
		}
		return clientIP;
	}

	private void flushResponse(ChannelHandlerContext ctx, GatewayHttpRequest request, GatewayHttpResponse response) {
		byte[] arrayBytes = null;
		Object body = response.getResponseBody();
		if (body != null) {
//			if (body instanceof MessageLite) {
//				arrayBytes = ((MessageLite) body).toByteArray();
//			} else if (body instanceof MessageLite.Builder) {
//				arrayBytes = ((MessageLite.Builder) body).build().toByteArray();
//			} else {
//				arrayBytes = new Gson().toJson(body).getBytes();
//			}
			arrayBytes = JSONUtils.filterJsonResponseBody(body).getBytes();
		} else {
			arrayBytes = new Gson().toJson("ERROR").getBytes();
		}
		ByteBuf res = Unpooled.copiedBuffer(arrayBytes);
		if (!writeResponse(ctx, request, res, response)) {
			ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
		}
	}

	private boolean writeResponse(ChannelHandlerContext ctx, GatewayHttpRequest request, ByteBuf result,
			GatewayHttpResponse _response) {
		HttpMessage msg = request.fullHttpRequest();
		boolean keepAlive = HttpUtil.isKeepAlive(msg);

		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, _response.getResponseStatus(), result);

		response.headers().set(HttpHeaderNames.CONTENT_TYPE, _response.getContentType());

		// 允许跨域访问
		response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
		response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "*");
		response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "GET,POST,PUT,DELETE");
		if (keepAlive) {
			response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
			// Add keep alive header as per:
			// http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
			response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
		}
		// Encode the cookie.
		String cookieString = msg.headers().get(HttpHeaderNames.COOKIE);
		if (cookieString != null) {
			Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieString);
			if (!cookies.isEmpty()) {
				for (Cookie cookie : cookies) {
					response.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie));
				}
			}
		}
		ctx.writeAndFlush(response);
		return keepAlive;
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		log.error("HttpProcessChannelHandler.exceptionCaught", cause);
		ctx.close();
	}

}