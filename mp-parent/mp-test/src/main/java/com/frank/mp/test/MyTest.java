package com.frank.mp.test;

public class MyTest {
	
	public static int test(int n) {
		n = (n & 0x55555555) + ((n >> 1) & 0x55555555);
		n = (n & 0x33333333) + ((n >> 1) & 0x33333333);
		n = (n & 0x0f0f0f0f) + ((n >> 1) & 0x0f0f0f0f);
		n = (n & 0x00ff00ff) + ((n >> 1) & 0x00ff00ff);
		n = (n & 0x0000ffff) + ((n >> 1) & 0x0000ffff);
		return n;
	}
	
	public static void main(String[] args) {
		System.out.println("1, "+test(1));
		System.out.println("2, "+test(2));
		System.out.println("3, "+test(3));
		System.out.println("4, "+test(4));
		System.out.println("5, "+test(5));
		System.out.println("6, "+test(6));
		System.out.println("7, "+test(7));
		System.out.println("8, "+test(8));
		System.out.println("9, "+test(9));
		System.out.println("10, "+test(10));
		System.out.println("11, "+test(11));
		System.out.println("12, "+test(12));
		System.out.println("20, "+test(20));
		System.out.println("100, "+test(100));
		System.out.println("200, "+test(200));
		System.out.println("101, "+test(101));
		
		System.out.println(Integer.reverse(128));
	}
}
