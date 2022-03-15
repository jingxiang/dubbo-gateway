package com.kalman03.gateway.netty.handler;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.kalman03.gateway.netty.NettyServer;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
/**
 * @author kalman03
 * @since 2022-03-15
 */
@Component
public class DefaultChannelInitializer extends ChannelInitializer<SocketChannel> {

	@Resource
	private HttpProcessChannelHandler httpProcessHandler;
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ch.pipeline().addLast(
				new IdleStateHandler(10, 10, 20, TimeUnit.SECONDS),
		        new HeartHandler(),
				new LoggingHandler(NettyServer.class, LogLevel.DEBUG), 
				new HttpServerCodec(),
				new HttpObjectAggregator(512 * 1024 * 1024), 
				httpProcessHandler);
	}
	
    public class HeartHandler extends ChannelDuplexHandler {
        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent e = (IdleStateEvent) evt;
                if (e.state() == IdleState.READER_IDLE) {
                    ctx.close();
                } else if (e.state() == IdleState.WRITER_IDLE) {
                    ctx.writeAndFlush("ping message....");
                } else if (e.state() == IdleState.ALL_IDLE) {
                    ctx.channel().close();
                }
            }
        }
    }
	
}