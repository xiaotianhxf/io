package com.frank.netty.fundametal.heartbeat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class HeartbeatServerHandler extends ChannelInboundHandlerAdapter {
	
	int tryCount = 3;

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (! (evt instanceof IdleStateEvent)) {
			return;
		}
		
		IdleStateEvent event = (IdleStateEvent) evt;
		if (event.state() == IdleState.READER_IDLE && tryCount--<0) {
			ctx.channel().close();
		} else {
			System.out.println("没有收到客户端信息，重试");
		}
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("关闭");
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println(ctx.channel().remoteAddress() + "->Server: " + msg.toString());
	}
}
