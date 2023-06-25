package de.jClipCorn.util.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("nls")
public class RegExHelper {
	public static boolean startsWithRegEx(String regEx, String input) {
		Matcher matcher = Pattern.compile(regEx).matcher(input);

		while (matcher.find()) {
			if (matcher.start() == 0) {
				return true;
			}
		}

		return false;
	}

	public static boolean startsWithRegEx(Pattern regEx, String input) {
		Matcher matcher = regEx.matcher(input);

		while (matcher.find()) {
			if (matcher.start() == 0) {
				return true;
			}
		}

		return false;
	}

	public static String find(String regEx, String input) {
		return find(regEx, input, 0);
	}

	public static String find(Pattern regEx, String input) {
		return find(regEx, input, 0);
	}

	public static String find(String regEx, String input, int group) {
		Matcher matcher = Pattern.compile(regEx).matcher(input);
		if (matcher.find()) {
			return matcher.group(group);
		}
		return "";
	}

	public static String find(Pattern regEx, String input, int group) {
		Matcher matcher = regEx.matcher(input);
		if (matcher.find()) {
			return matcher.group(group);
		}
		return "";
	}

	public static List<String> findAll(String regEx, String input) {
		List<String> result = new ArrayList<>();

		Matcher matcher = Pattern.compile(regEx).matcher(input);

		while (matcher.find()) {
			result.add(matcher.group());
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
		Matcher matcher = Pattern.compile(regEx).matcher(input);

		if (matcher.find()) {
			return stringReplace(input, matcher.start(), matcher.end(), replace);
		} else {
			return input;
		}
	}

	public static String replace(Pattern regEx, String input, String replace) {
		Matcher matcher = regEx.matcher(input);

		if (matcher.find()) {
			return stringReplace(input, matcher.start(), matcher.end(), replace);
		} else {
			return input;
		}
	}

	public static boolean isPatternMatch(Pattern p, String s) {
		Matcher m = p.matcher(s);

		return m.matches();
	}

	private static String stringReplace(String input, int start, int end, String replace) {
		return "".concat(input.substring(0, start)).concat(replace).concat(input.substring(end));
	}

	public static String getGroup(Pattern regex, String content, String group) {
		Matcher m = regex.matcher(content);

		if (!m.matches()) return null;

		return m.group(group);
	}

	public static boolean isMatch(String regex, String str) {
		return Pattern.compile(regex).matcher(str).matches();
	}
}
