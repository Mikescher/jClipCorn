package de.jClipCorn.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("nls")
public class RegExHelper {
	public static boolean startsWithRegEx(String regEx, String input) {
		Matcher m = Pattern.compile(regEx).matcher(input);

		while (m.find()) {
			if (m.start() == 0) {
				return true;
			}
		}

		return false;
	}

	public static String find(String regEx, String input) {
		Matcher m = Pattern.compile(regEx).matcher(input);
		if (m.find()) {
			return m.group();
		}
		return "";
	}

	public static ArrayList<String> findAll(String regEx, String input) {
		ArrayList<String> result = new ArrayList<>();

		Matcher m = Pattern.compile(regEx).matcher(input);

		while (m.find()) {
			result.add(m.group());
		}

		return result;
	}

	public static String replaceAll(String regEx, String input, String replace) {
		String curr = null;
		String newcurr = input;

		do {
			curr = newcurr;
			newcurr = replace(regEx, curr, replace);
		} while (!curr.equals(newcurr));

		return newcurr;
	}

	public static String replace(String regEx, String input, String replace) {
		Matcher m = Pattern.compile(regEx).matcher(input);

		if (m.find()) {
			return stringReplace(input, m.start(), m.end(), replace);
		} else {
			return input;
		}
	}

	private static String stringReplace(String input, int start, int end, String replace) {
		return "".concat(input.substring(0, start)).concat(replace).concat(input.substring(end));
	}
}
