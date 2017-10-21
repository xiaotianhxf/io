package com.frank.netty.fundametal.reconnect;

import java.util.Date;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.util.ReferenceCountUtil;

@Sharable
public class HeartBeatClientHandler extends ChannelInboundHandlerAdapter {
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("����ʱ���ǣ�"+ new Date());
		System.out.println("HearbeatClientHandler channelActive");
		ctx.fireChannelActive();
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("ֹͣʱ���ǣ�" + new Date());
		System.out.println("HearbeatClientHandler channelInActive");
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		String message = (String) msg;
		System.out.println(message);
		if (message.equals("Heartbeat")) { 
			ctx.write("has read message from server");  
			ctx.flush();  
		}
		ReferenceCountUtil.release(msg);  
	}

}
