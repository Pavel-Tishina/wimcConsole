package com.tishina.wimcConsole.utils;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class TextUtils {

	public static String encodeFromTo(String from, String to, String txt) {
		try {
			return new String(txt.getBytes(from), to);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
				
		}
	}
	
	public static String encodeFromTo2(String from, String to, String txt) {
		Charset in = Charset.forName(from);
		Charset out = Charset.forName(to);
		
		return out.decode(ByteBuffer.wrap(txt.getBytes())).toString();
	}
	
	public static String encodeFromTo3(String from, String to, String txt) {
		Charset inCP = Charset.forName(from);
		Charset outCP = Charset.forName(to);
		
		byte[] in = txt.getBytes(inCP);
		String inStr = new String(in, inCP);
		
	}
}
