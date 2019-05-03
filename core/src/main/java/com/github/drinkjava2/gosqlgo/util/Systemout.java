package com.github.drinkjava2.gosqlgo.util;

/**
 * Debug Util
 * 
 * @author Yong Zhu
 * @since 2.0.5
 */
public class Systemout {
	private static final boolean ALLOW_PRINT = true;

	public static void print(Object obj) {
		if (ALLOW_PRINT)
			System.out.println(obj);
	}

	public static void println(Object obj) {
		if (ALLOW_PRINT)
			System.out.println(obj);
	}

	public static void println() {
		if (ALLOW_PRINT)
			System.out.println();
	}
}
