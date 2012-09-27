package com.ourlittlegame.utilities;

import android.widget.TextView;

public class MiscUtils {
	private static boolean enableLogging = true;	// should be read from config ideally
	public static void println(String s) {
		if (enableLogging)
			System.out.println(s);
	}
	public static void println(double s) {
		if (enableLogging)
			System.out.println(s);
	}
	public static String removeNull(String str) {
		if (str == null)
			return "";
		return str;
	}
}
