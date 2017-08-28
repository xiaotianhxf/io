package com.frank.mp.proxy.test.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class MyNIOTest {
	private static String hostname = "127.0.0.1";

	public static void main(String[] args) throws IOException {
		ServerSocketChannel ssc = ServerSocketChannel.open();
		int port = 3307;
		ssc.configureBlocking(false);
		ssc.socket().bind(new InetSocketAddress(hostname, port));
		Selector selector = Selector.open();
		ssc.register(selector, SelectionKey.OP_ACCEPT);
		
		while (true) {
			int num = selector.select(3000);
			while (num <= 0) {
				System.out.println("continue, and num is: " + num);
				num = selector.select(3000);
				continue;
			}
			
			byte[] bytes = openServerConnection();	
			
			Iterator<SelectionKey> iters = selector.selectedKeys().iterator();
			while (iters.hasNext()) {
				SelectionKey selectionKey = (SelectionKey) iters.next();
				if (selectionKey.isAcceptable()) {
					handleAccept(bytes, selectionKey);
				}
				
			}
		}
	}
	
	public static void handleAccept(byte[] bytes, SelectionKey selectionKey) throws IOException {
		ServerSocketChannel ssc = (ServerSocketChannel) selectionKey.channel();
		ServerSocket ss = ssc.socket();
		Socket socket = ss.accept();
		byte[] readBytes = new byte[4096];
		socket.getInputStream().read(readBytes);
		socket.getOutputStream().write(bytes);
		socket.getOutputStream().flush();
	}
	
	public static byte[] openServerConnection() throws IOException {
		SocketChannel socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(false);
		socketChannel.connect(new InetSocketAddress(hostname, 3306));
		
		ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
		
		if (socketChannel.finishConnect()) {
			socketChannel.read(byteBuffer);
			byteBuffer.flip();
		}
		System.out.println(byteBuffer.limit());
		byte[] bytes = new byte[4096];
		byteBuffer.get(bytes, 0, byteBuffer.limit());
		System.out.println(new String(bytes));
		return bytes;
	}
}
