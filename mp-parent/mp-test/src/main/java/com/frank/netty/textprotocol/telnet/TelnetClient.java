package com.frank.netty.textprotocol.telnet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TelnetClient {
	public static void main(String[] args) throws InterruptedException, IOException {
		EventLoopGroup group = new NioEventLoopGroup();
		
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class)
				.handler(new TelnetClientInitializer());
			
			Channel ch = b.connect("127.0.0.1", 8007).sync().channel();
			
			ChannelFuture lastWriteFuture = null;
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			
			for (;;) {
				String line = in.readLine();
				if (line == null) {
					break;
				}
				
				lastWriteFuture = ch.writeAndFlush(line + "\r\n");
				
				if ("byte".equals(line.toLowerCase())) {
					ch.closeFuture().sync();
					break;
				}
			}
			
			if (lastWriteFuture != null) {
				lastWriteFuture.sync();
			}
		} finally {
			group.shutdownGracefully();
		}
	}
}
