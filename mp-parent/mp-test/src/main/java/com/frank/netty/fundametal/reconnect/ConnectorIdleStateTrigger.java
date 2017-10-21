package com.frank.netty.fundametal.reconnect;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

@Sharable
public class ConnectorIdleStateTrigger extends ChannelInboundHandlerAdapter{
	private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Heartbeat",CharsetUtil.UTF_8));  
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (!(evt instanceof IdleStateEvent)) {
			super.userEventTriggered(ctx, evt);
		}
		
		IdleStateEvent event = (IdleStateEvent) evt;
		if (event.state() == IdleState.WRITER_IDLE) {
			ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate());
		}
	}
}
