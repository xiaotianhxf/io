package com.frank.mp.proxy.test.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;

public class NioServer {
	private int port;
	private ServerSocketChannel ssc;
	private static String hostname = "127.0.0.1";
	private Selector selector;
	private int timeout = 3000;
	private String message = "";
	
	public NioServer(int port) throws IOException {
		this.port = port;
		init();
		doSelect();
	}
	
	private void init() throws IOException {
		ssc = ServerSocketChannel.open();
		ssc.configureBlocking(false);
		ssc.socket().bind(new InetSocketAddress(hostname, this.port));
		selector = Selector.open();
		ssc.register(selector, SelectionKey.OP_ACCEPT);
	}
	
	private void doSelect() throws IOException {
		int select = this.selector.select(timeout);
		while (true) {
			if (select == 0) {
				System.out.println("nioServer timeout, select is: " + select);
				select = this.selector.select(timeout);
				continue;
			}
			Iterator<SelectionKey> iters = this.selector.selectedKeys().iterator();
			while (iters.hasNext()) {
				SelectionKey selectionKey = (SelectionKey) iters.next();
				iters.remove();
				if (selectionKey.isAcceptable()) {
					handleAccept(selectionKey);
				}
				
				if (selectionKey.isReadable()) {
					handleRead(selectionKey);
				}
				
				if (selectionKey.isWritable()) {
					handleWrite(selectionKey);
				}
				
			}
			select = this.selector.select(timeout);
		}
	}
	
	private void handleAccept(SelectionKey selectionKey) throws IOException {
		ServerSocketChannel ssc = (ServerSocketChannel) selectionKey.channel();
		SocketChannel socketChannel = ssc.accept();
		socketChannel.configureBlocking(false);
		socketChannel.register(this.selector, SelectionKey.OP_READ);
	}
	
	private void handleRead(SelectionKey selectionKey) throws IOException {
		SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
		ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
		byteBuffer.clear();
		int count = socketChannel.read(byteBuffer);
		byteBuffer.flip();
		if (count > 0) {
			System.out.println("reciever: "+new String(byteBuffer.array(), 0, count));
		}
		
		socketChannel.register(this.selector, SelectionKey.OP_WRITE);
	}
	
	private void handleWrite(SelectionKey selectionKey) throws IOException {
		SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
		ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
		byteBuffer.put(("this is server info, now is "+new Date()).getBytes());
		byteBuffer.flip();
		socketChannel.write(byteBuffer);
		socketChannel.register(this.selector, SelectionKey.OP_READ);
	}
	
	public static void main(String[] args) {
		try {
			new NioServer(4000);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
