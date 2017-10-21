package com.frank.netty.textprotocol.telnet;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class TelnetClientInitializer extends ChannelInitializer<SocketChannel>{

	private static final StringDecoder DECODER = new StringDecoder();
	private static final StringEncoder ENCODER = new StringEncoder();	
	
	private static final TelnetClientHandler CLIENT_HANDLER = new TelnetClientHandler();
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline p = ch.pipeline();
		
		 // Add the text line codec combination first,
		p.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
		p.addLast(DECODER);
		p.addLast(ENCODER);
		
		// and then business logic
		p.addLast(CLIENT_HANDLER);
	}

}
