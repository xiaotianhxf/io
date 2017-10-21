package com.frank.netty.fundametal.reconnect;

import io.netty.channel.ChannelHandler;

public interface ChannelHandlerHolder {
	ChannelHandler[] handlers();
}
