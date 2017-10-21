package com.frank.netty.textprotocol.broadcast;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class QueteOfTheMomentServer {
	public static void main(String[] args) throws InterruptedException {
		EventLoopGroup group = new NioEventLoopGroup();
		
		try {
			Bootstrap b = new Bootstrap();
			b.group(group)
				.channel(NioDatagramChannel.class)
				.option(ChannelOption.SO_BROADCAST, true)
				.handler(new QueteOfTheMomentServerHanlder());
			
			b.bind(7686).sync().channel().closeFuture().await();
		} finally {
			group.shutdownGracefully();
		}
	}
}
