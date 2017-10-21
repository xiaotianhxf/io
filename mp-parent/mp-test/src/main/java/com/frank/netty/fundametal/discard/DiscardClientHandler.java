package com.frank.netty.fundametal.discard;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class DiscardClientHandler extends ChannelInboundHandlerAdapter{

	private ByteBuf content;
	private ChannelHandlerContext ctx;
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		this.ctx = ctx;
		//initialize the message
		content = ctx.alloc().directBuffer(DiscardClient.SIZE).writeZero(DiscardClient.SIZE);
		
		//send the initial messages
		generateTraffic();
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		content.release();
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		//server is supposed to send nothing, but if it send something, discard it.
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
	
	long counter;

	private void generateTraffic() {
		// Flush the outbound buffer to the socket.
		// Once flushed, generate the same amount of traffic again.
		ctx.writeAndFlush(content.retainedDuplicate()).addListener(trafficGenerator);		
	}
	
	private final ChannelFutureListener trafficGenerator = new ChannelFutureListener() {
		
		public void operationComplete(ChannelFuture future) throws Exception {
			if (future.isSuccess()) {
				generateTraffic();
			} else {
				future.cause().printStackTrace();
				future.channel().close();
			}
		}
	};
}
