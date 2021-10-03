package de.jClipCorn.util;

import de.jClipCorn.util.datatypes.Opt;
import org.apache.commons.codec.binary.Base64;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;

@SuppressWarnings("nls")
public final class Str {
	public final static String Empty = "";
	public final static String SingleSpace = " ";

	public static Charset UTF8 = StandardCharsets.UTF_8;
	
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

	public static String trim(String str) {
		return str == null ? null : str.trim();
	}

	public static boolean equals(String a, String b) {
		if (a == null) return (b == null);
		return a.equals(b);
	}

	public static boolean equalsIgnoreCase(String a, String b) {
		if (a == null) return (b == null);
		return a.equalsIgnoreCase(b);
	}

	public static String toBase64(String v) {
		return Base64.encodeBase64String(v.getBytes(UTF8));
	}
	
	public static String fromBase64(String v) {
		return new String(Base64.decodeBase64(v), UTF8);
	}

	public static String safeSubstring(String s, int start, int len)
	{
		if (start > s.length()) return Str.Empty;

		return s.substring(start, Math.min(s.length(), start + len));
	}

	public static String coalesce(String str) {
		if (str == null) return Empty;
		return str;
	}

	public static String limit(String str, int max) {
		if (str == null) return null;
		if (str.length()<=max)return str;
		return str.substring(0, max-3)+"...";
	}

	public static String spacegroupformat(int v) {
		String a = format("{0,number,#}",v);
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < a.length(); i++) {
			if (i%3==0 && i>0) b.insert(0, ' ');
			b.insert(0, a.charAt(a.length() - i - 1));
		}
		return b.toString();
	}

	public static boolean isInteger(String val) {
		try	{ Integer.parseInt(val); return true; } catch (NumberFormatException e) { return false; }
	}

	public static boolean isDouble(String val) {
		try	{ Double.parseDouble(val); return true; } catch (NumberFormatException e) { return false; }
	}

	public static Opt<Integer> tryParseInt(String val)
	{
		try	{ return Opt.of(Integer.parseInt(val)); } catch (NumberFormatException e) { return Opt.empty(); }
	}

	public static Opt<Double> tryParseDouble(String val)
	{
		try	{ return Opt.of(Double.parseDouble(val)); } catch (NumberFormatException e) { return Opt.empty(); }
	}

	public static String toProperCase(String str) {
		StringBuilder b = new StringBuilder(str.length());

		boolean uc = true;
		for (int i=0; i<str.length(); i++) {
			char c = str.charAt(i);
			if (c==' ') {
				uc = true;
				b.append(c);
			} else {
				b.append(uc ? Character.toUpperCase(c) : Character.toLowerCase(c));
				uc = false;
			}
		}

		return b.toString();
	}

	public static boolean isUntrimmed(String s) {
		return !s.equals(s.trim());
	}

	public static String repeat(@NotNull String str, int count) {
		return str.repeat(count);
	}
}
