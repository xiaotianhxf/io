package com.frank.netty.fundametal.uptime;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

public class UptimeClient {
	static final String HOST = "127.0.0.1";
	static final int POST = 8080;
	//5 seconds
	static final int RECONNECT_DELAY = 5;
	//10 seconds
	static final int READ_TIMEOUT = 10;
	
	private static final UptimeClientHandler handler = new UptimeClientHandler();
	private static final Bootstrap b = new Bootstrap();
	
	public static void main(String[] args) {
		EventLoopGroup group = new NioEventLoopGroup();
		
		b.group(group).channel(NioSocketChannel.class)
			.remoteAddress(HOST, POST)
			.handler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new IdleStateHandler(READ_TIMEOUT, 0, 0), handler);
				}
			});
		
		b.connect();
	}
	
	static void connect(){
		b.connect().addListener(new ChannelFutureListener() {
			
			public void operationComplete(ChannelFuture future) throws Exception {
				if (future.cause() != null) {
					handler.startTime = -1;
					handler.println("Failed to Connect:" + future.cause());
				}
			}
		});
	}
	
}
