package com.frank.mp.proxy.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SelectionKey;

public class MyProxyServer {
	
	public static void main(String[] args) throws Exception{
		ServerSocket ss = new ServerSocket();
		int port = 3308;
		SocketAddress socketAddress = new InetSocketAddress(port); 
		ss.bind(socketAddress);
		boolean isConnection = true;
		while (true) {
			Socket socket = ss.accept();
//			InputStream proxyInput = createProxy(null);
//			OutputStream out = socket.getOutputStream();
//			InputStream input = socket.getInputStream();
//			byte[] bytes = new byte[1024];
//			int i = input.read();
//			System.out.println(i);
//			
//			bytes = new byte[1024];
//			proxyInput.read(bytes);
//			
			createProxy1(socket, isConnection);
			isConnection = false;
//			out.write("".getBytes());;
//			input.close();
//			out.close();
//			socket.close();
		}
	}
	public static void createProxy1(Socket socket, boolean isConnection) throws IOException {
		if (proxySocket == null) {
			proxySocket = new Socket("127.0.0.1", 3306);
		}
		InputStream clientInput = socket.getInputStream();
		OutputStream clientOut = socket.getOutputStream();
		
		InputStream serverInput = proxySocket.getInputStream();
		OutputStream serverOut = proxySocket.getOutputStream();
		
		if (isConnection) {
			byte[] bytes = new byte[2048];
			int len = serverInput.read(bytes);
			clientOut.write(bytes, 0, len);
			clientOut.flush();
		}else {
			byte[] bytes = new byte[2048];
			int len = clientInput.read(bytes);
			serverOut.write(bytes, 0, len);
			serverOut.flush();
			len = serverInput.read(bytes);
			clientOut.write(bytes,0,len);
			clientOut.flush();
		}
		
//		socket.close();
//		proxySocket.close();
		
//		System.out.println(new String(bytes));
//		socket.getOutputStream().write(bytes);
//		socket.getOutputStream().flush();
//		proxySocket.close();
//		socket
//		socket.connect(endpoint);
		
	}
	
	public static Socket proxySocket = null;
	public static InputStream createProxy(byte[] bytes) throws UnknownHostException, IOException {
		if (proxySocket == null) {
			new Socket("127.0.0.1", 3306);
		}
		try {
			if (bytes!=null) {
				proxySocket.getOutputStream().write(bytes);
			}
			return proxySocket.getInputStream();
		} finally {
//			proxySocket.close();
		}
		
	}
}
