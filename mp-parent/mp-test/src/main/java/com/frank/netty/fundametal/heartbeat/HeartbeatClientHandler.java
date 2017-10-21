package com.frank.netty.fundametal.heartbeat;

import java.util.Date;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

public class HeartbeatClientHandler extends ChannelInboundHandlerAdapter{
	
	private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Heartbeat",  
            CharsetUtil.UTF_8)); 
	int tryCount = 3;
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("激活时间： " + new Date());
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("关闭时间：" + new Date());
	}
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (! (evt instanceof IdleStateEvent)) {
			return;
		}
		IdleStateEvent event = (IdleStateEvent) evt;
		if (event.state() == IdleState.WRITER_IDLE && tryCount-- >0) {
			System.out.println("没有输出,输出心跳");
			ctx.channel().writeAndFlush(HEARTBEAT_SEQUENCE.duplicate());
		}
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		String message = (String) msg;
		System.out.println(message);
	}
}
