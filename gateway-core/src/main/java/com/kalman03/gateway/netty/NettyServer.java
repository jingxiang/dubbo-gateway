package com.kalman03.gateway.netty;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.kalman03.gateway.netty.handler.DefaultChannelInitializer;
import com.kalman03.gateway.utils.InetAddressUtils;
import com.kalman03.gateway.utils.NamingThreadFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ResourceLeakDetector;
import lombok.extern.slf4j.Slf4j;

/**
 * @author kalman03
 * @since 2022-03-15
 */
@Slf4j
@Component
public class NettyServer implements SmartInitializingSingleton {

	private ServerBootstrap bootstrap;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	private final ExecutorService serverStartor = Executors
			.newSingleThreadExecutor(new NamingThreadFactory("Dubbo-Gateway-Starter"));

	@Value("${gateway.netty.server.port:80}")
	private int port;
	@Value("${gateway.netty.server.host:}")
	private String host;
	@Value("${gateway.netty.server.connect-timeout:5000}")
	private int connectTimeout;

	@Resource
	private DefaultChannelInitializer defaultChannelInitializer;

	@Override
	public void afterSingletonsInstantiated() {
		serverStartor.execute(() -> {
			init();
			if (isBlank(host)) {
				host = InetAddressUtils.getLocalIP();
			}
			try {
				ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
				ChannelFuture f = bootstrap.bind(host, port).sync();
				log.info("dubbo gateway started, host is {} , port is {}.", host, port);
				f.channel().closeFuture().sync();
				log.info("dubbo gateway closed, host is {} , port is {}.", host, port);
			} catch (InterruptedException e) {
				log.error("dubbo gateway start failed", e);
			} finally {
				destroy();
			}
		});
	}

	private void init() {
		bootstrap = new ServerBootstrap();
		int availableProcessors = Runtime.getRuntime().availableProcessors();
		bossGroup = new NioEventLoopGroup(availableProcessors, new NamingThreadFactory("Gateway-Boss"));
		workerGroup = new NioEventLoopGroup(availableProcessors * 2, new NamingThreadFactory("Gateway-Work"));

		bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
				.childHandler(defaultChannelInitializer)
				.childOption(ChannelOption.TCP_NODELAY, true)
				.childOption(ChannelOption.SO_KEEPALIVE, true)
				.childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout);
	}

	@PreDestroy
	public void destroy() {
		if (workerGroup != null) {
			workerGroup.shutdownGracefully();
		}
		if (bossGroup != null) {
			bossGroup.shutdownGracefully();
		}
		serverStartor.shutdown();
	}

}
