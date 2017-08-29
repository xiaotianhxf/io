package com.frank.mp.proxy.test.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;

public class NioClient {
	private String hostname;
	private int port;
	private Socket socket;
	private Selector selector;
	private int timeout = 3000;

	public NioClient(String hostname, int port) throws IOException {
		this.hostname = hostname;
		this.port = port;
		init();
		select();
	}
	
	private void init() throws IOException {
		SocketChannel socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(false);
		selector = Selector.open();
		socketChannel.register(selector, SelectionKey.OP_CONNECT);
		socketChannel.connect(new InetSocketAddress(hostname, port));
	}
	
	private void select() throws IOException {
		int select = this.selector.select(timeout);
		while (true) {
			if (select <= 0) {
				System.out.println("client select timout");
				select = this.selector.select(timeout);
				continue;
			}
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Iterator<SelectionKey> iters = selector.selectedKeys().iterator();
			while (iters.hasNext()) {
				SelectionKey selectionKey = iters.next();
				iters.remove();
				SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
				
				if (selectionKey.isConnectable()) {
					if (socketChannel.finishConnect()) {
						System.out.println("连接成功");
					}
					ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
					byteBuffer.put("client ask,now is ?".getBytes());
					byteBuffer.flip();
					socketChannel.write(byteBuffer);
					socketChannel.register(selector, SelectionKey.OP_READ);
				}
				
				if (selectionKey.isReadable()) {
					ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
					int count = socketChannel.read(byteBuffer);
					byteBuffer.flip();
					if (count>0) {
						System.out.println(new String(byteBuffer.array(), 0, count));
					}
					socketChannel.register(selector, SelectionKey.OP_WRITE);
				}
				
				if (selectionKey.isWritable()) {
					ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
					byteBuffer.put(("client ask,now is ?").getBytes());
					byteBuffer.flip();
					socketChannel.write(byteBuffer);
					socketChannel.register(selector, SelectionKey.OP_READ);
				}
			}
			
			select = this.selector.select(timeout);
		}
	}
	
	public static void main(String[] args) {
		try {
			new NioClient("127.0.0.1", 4000);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
