package com.frank.netty.fundametal.reconnect;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.HashedWheelTimer;

public class HeartbeatClient {
	protected final HashedWheelTimer timer = new HashedWheelTimer();
	
	private Bootstrap bootstrap;
	
	private final ConnectorIdleStateTrigger idleStateTrigger = new ConnectorIdleStateTrigger();
	
	public void connect(int port, String host) throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		
		bootstrap = new Bootstrap();
		bootstrap.group(group).channel(NioSocketChannel.class).handler(new LoggingHandler(LogLevel.INFO));
		
		final ConnectionWatchdog watchdog = new ConnectionWatchdog(bootstrap, timer, host, port, true) {
			
			public ChannelHandler[] handlers() {
				return new ChannelHandler[]{
					this, new IdleStateHandler(0, 4, 0, TimeUnit.SECONDS), idleStateTrigger, new HeartBeatClientHandler()	
				};
			}
		};
		
		ChannelFuture future;
		
		try {
			synchronized (bootstrap) {
				bootstrap.handler(new ChannelInitializer<Channel>() {

					@Override
					protected void initChannel(Channel ch) throws Exception {
						ch.pipeline().addLast(watchdog.handlers());
					}
				});
				
				future = bootstrap.connect(host, port);
			}
			future.sync();
		} catch(Throwable t) {
			throw new Exception("Connect to fails", t);
		}
	}
	
	public static void main(String[] args) throws Exception {
		int port = 8080;
		if (args != null && args.length >0) {
			try {
				port = Integer.valueOf(args[0]);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		new HeartbeatClient().connect(port, "127.0.0.1");
	}
}
