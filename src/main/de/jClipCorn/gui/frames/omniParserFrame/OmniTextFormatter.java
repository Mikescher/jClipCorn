package de.jClipCorn.gui.frames.omniParserFrame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.jClipCorn.util.filesystem.SimpleFileUtils;

public class OmniTextFormatter {
	public static String format(String input, boolean splitLongLines) {
		List<String> list = formatList(input, splitLongLines);

		StringBuilder result = new StringBuilder();

		for (int i = 0; i < list.size(); i++) {
			if (i > 0)
				result.append(SimpleFileUtils.LINE_END);
			result.append(list.get(i));
		}

		return result.toString();
	}

	private static List<String> formatList(String input, boolean splitLongLines) {
		List<String> list = new ArrayList<>(Arrays.asList(SimpleFileUtils.splitLines(input)));

		trimList(list);
		
		while (cleanUpList(list) > 0) {
			// repeat until cleanUpList == 0
		}

		removeShortLiner(list);

		if (list.size() == 1 && splitLongLines) {
			list = splitLine(list.get(0));
			cleanUpList(list);
		}

		return list;
	}

	private static int cleanUpList(List<String> list) {
		int delLines = 0;

		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).length() < 3) {
				list.set(i, ""); //$NON-NLS-1$
			}
		}

		for (int i = 0; i < list.size(); i++) {
			String s = list.get(i);

			boolean charFound = false;
			for (int j = 0; j < s.length(); j++) {
				if (Character.isAlphabetic(s.charAt(j)) || Character.isDigit(s.charAt(j)))
					charFound = true;
			}

			if (!charFound)
				list.set(i, ""); //$NON-NLS-1$
		}

		for (int i = list.size() - 1; i >= 0; i--) {
			if (list.get(i).isEmpty()) {
				list.remove(i);
				delLines++;
			}
		}

		return delLines;
	}

	private static void removeShortLiner(List<String> list) {
		int longestLine = 0;

		for (int i = 0; i < list.size(); i++) {
			longestLine = Math.max(longestLine, list.get(i).length());
		}

		int minLineLen = longestLine / 4;

		for (int i = list.size() - 1; i >= 0; i--) {
			if (list.get(i).length() < minLineLen) {
				list.remove(i);
			}
		}
	}

	private static List<String> splitLine(String line) {
		String splitter = findBestSplitter(line);

		if (splitter != null) {
			List<String> result = new ArrayList<>();
			int lastpos = 0;
			int pos = line.indexOf(splitter);

			while (pos >= 0) {
				result.add(line.substring(lastpos, pos));

				lastpos = pos;
				pos = line.indexOf(splitter, lastpos + 1);
			}

			result.add(line.substring(lastpos));

			return result;
		} else {
			List<String> result = new ArrayList<>();
			result.add(line);
			return result;
		}
	}

	private static String findBestSplitter(String line) {
		String splitter = findBestSplitterFromStart(line);

		if (splitter == null) {
			splitter = findBestSplitterFromMiddle(line);
		}

		return splitter;
	}

	private static String findBestSplitterFromStart(String line) {
		int len = 3;
		int maxSubsVal = -1;
		String subs = null;

		while (len < (line.length() / 2)) {
			String substr = line.substring(0, len);
			int subcount = countSubstrings(line, substr);

			if (subcount > 3) {
				int val = 2 * subcount + len;

				if (val > maxSubsVal) {
					subs = substr;
					maxSubsVal = val;
				}
			}
			
			len++;
		}

		return subs;
	}

	private static String findBestSplitterFromMiddle(String line) {
		String splitter = null;
		int splitterVal = -1;

		for (int pos = 1; pos < line.length(); pos++) {
			for (int len = 3; len < line.length() / 3; len++) {
				if (pos + len < line.length()) {
					String testsplitter = line.substring(pos, pos + len);
					int substrings = countSubstrings(line, testsplitter);
					if (substrings > 3) {
						int val = 2 * substrings + len;
						if (val > splitterVal) {
							splitter = testsplitter;
							splitterVal = val;
						}
					}
				}
			}
		}

		return splitter;
	}

	private static int countSubstrings(String str, String substr) {
		int occs = 0;

		int pos = 0;

		while ((pos = str.indexOf(substr, pos + 1)) != -1) {
			occs++;
		}

		return occs;
	}
	
	private static void trimList(List<String> list) {
		for (int i = 0; i < list.size(); i++) {
			list.set(i, list.get(i).trim());
		}
	}
}
