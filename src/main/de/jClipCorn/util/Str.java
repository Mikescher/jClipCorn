package de.jClipCorn.util;

import java.nio.charset.Charset;
import java.text.MessageFormat;

import org.apache.commons.codec.binary.Base64;

@SuppressWarnings("nls")
public final class Str {
	public final static String Empty = "";

	public static Charset UTF8 = Charset.forName("UTF-8");
	
	public static String format(String fmt, Object... data) {
		return MessageFormat.format(fmt, data);
	}

	public static String firstLine(String content) {
		int idx = content.indexOf('\n');
		if (idx == -1) return content;

		if (idx == 0) return Str.Empty;

		if (content.charAt(idx-1) == '\r') return content.substring(0, idx-1);

		return content.substring(0, idx);
	}

	public static boolean isNullOrWhitespace(String str) {
		return str == null || str.length() == 0 || str.trim().length() == 0;
	}

	public static boolean isNullOrEmpty(String str) {
		return str == null || str.length() == 0;
	}

	public static boolean equals(String a, String b) {
		if (a == null) return (b == null);
		return a.equals(b);
	}
	
	public static String toBase64(String v) {
		return Base64.encodeBase64String(v.getBytes(UTF8));
	}
	
	public static String fromBase64(String v) {
		return new String(Base64.decodeBase64(v), UTF8);
	}
}
