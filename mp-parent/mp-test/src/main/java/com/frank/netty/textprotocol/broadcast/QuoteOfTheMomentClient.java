package com.frank.netty.textprotocol.broadcast;


import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.SocketUtils;

public class QuoteOfTheMomentClient {
	public static void main(String[] args) throws InterruptedException {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioDatagramChannel.class)
				.option(ChannelOption.SO_BROADCAST, true)
				.handler(new QueteOfTheMomentClientHandler());
			
			Channel ch = b.bind(0).sync().channel();
			
			// Broadcast the QOTM request to port 8080.
			ch.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer("QOTM?", CharsetUtil.UTF_8), 
					SocketUtils.socketAddress("255.255.255.255", 7686))).sync();
			
			if (!ch.closeFuture().await(5000)) {
				System.err.println("QOTM request time out");
			}
		} finally {
			group.shutdownGracefully();
		}
	}
}
