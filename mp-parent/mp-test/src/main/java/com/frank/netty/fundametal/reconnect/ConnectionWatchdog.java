package com.frank.netty.fundametal.reconnect;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

@Sharable
public abstract class ConnectionWatchdog extends ChannelInboundHandlerAdapter implements TimerTask, ChannelHandlerHolder{

	private final Bootstrap bootstrap;
	private final Timer timer;
	private final int port;
	private final String hosts;
	
	private volatile boolean reconnect = true;
	private int attempts;
	
	public ConnectionWatchdog(Bootstrap bootstrap, Timer timer, String hosts, int port, boolean reconnect) {
		this.bootstrap = bootstrap;
		this.timer = timer;
		this.hosts = hosts;
		this.port = port;
		this.reconnect = reconnect;
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("当前链路已激活，重置重试次数为0");
		attempts = 0;
		ctx.fireChannelActive();
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("链路关闭..");
		if (reconnect) {
			System.out.println("链路关闭，将执行重连。。");
			if (attempts < 12) {
				attempts ++;
				//重连的间隔时间越来越长
				int timeout = 2 << attempts;
				timer.newTimeout(this, timeout, TimeUnit.MILLISECONDS);
			}
		}
		ctx.fireChannelInactive();
	}
	
	public void run(Timeout timeout) throws Exception {
		ChannelFuture future;
		
		synchronized (bootstrap) {
			bootstrap.handler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(handlers());
				}
			});
			
			future = bootstrap.connect(hosts, port);
		}
		
		future.addListener(new ChannelFutureListener() {
			
			public void operationComplete(ChannelFuture future) throws Exception {
				boolean succeed = future.isSuccess();
				if (!succeed) {
					System.out.println("重连失败");
					future.channel().pipeline().fireChannelInactive();
				} else {
					System.out.println("重试成功");
				}
			}
		});
	}
	
}
