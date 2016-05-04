package de.jClipCorn.gui.frames.omniParserFrame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import de.jClipCorn.util.helper.TextFileUtils;

public class OmniTextParser {

	@SuppressWarnings("nls")
	private final static String[] INFORMATION_STRINGS = {
		"dubbed", 
		"xvid", 
		"telesync",
		"bdrip",
		"german",
		"english",
		"ws",
		"ts",
		"dl",
		"ld",
		"1080p",
		"720p",
		"x264",
		"dvdrip",
		"ac3d",
		"ac3ld",
		"hdtvrip",
		"hddvdrip",
		"hdbdrip",
		"ac3",
		"uncut",
		"hdts",
		"extended",
		"cut",
		"avi",
		"mpg",
		"mpeg",
		"mp4"
	};
	
	public static String parse(String input, boolean replaceUmlauts, boolean removeInfoStrings, boolean replaceSpaceAlts, boolean removeCommons) {
		List<String> list = parseList(input, replaceUmlauts, removeInfoStrings, replaceSpaceAlts, removeCommons);

		StringBuilder result = new StringBuilder();

		for (int i = 0; i < list.size(); i++) {
			if (i > 0)
				result.append(TextFileUtils.LINE_END);
			result.append(list.get(i));
		}

		return result.toString();
	}

	private static List<String> parseList(String input, boolean replaceUmlauts, boolean removeInfoStrings, boolean replaceSpaceAlts, boolean removeCommons) {
		List<String> list = new ArrayList<>(Arrays.asList(TextFileUtils.splitLines(input)));
		
		trimList(list);
		cleanUpList(list);
		
		if (removeCommons) {
			removeCommonStartings(list);
			removeCommonEndings(list);
		}
		
		trimList(list);
		cleanUpList(list);
		
		if (removeCommons) {
			removeDynamicCounterStartings(list);
			removeStaticCounterStartings(list);
			removeCounterEndings(list);
		}
		
		trimList(list);
		cleanUpList(list);
		
		if (removeCommons) {
			removeCommonStartings(list);
			removeCommonEndings(list);
		}
		
		if (replaceSpaceAlts) {
			replaceSpaceAlternatives(list);
		}
		
		trimList(list);
		cleanUpList(list);
		
		if (removeCommons) {
			removeCommonStartings(list);
			removeCommonEndings(list);
		}
		
		trimList(list);
		cleanUpList(list);
		
		if (removeInfoStrings) {
			removeInformationWordsFromList(list);
		}
		if (replaceUmlauts) {
			replaceUmlauts(list);
		}
		
		removeDoubleSpaces(list);

		return list;
	}

	private static int getMinLen(List<String> lines) {
		int min = Integer.MAX_VALUE;
		for (int i = 0; i < lines.size(); i++) {
			min = Math.min(min, lines.get(i).length());
		}
		return min;
	}

	private static String getCommonStartings(List<String> lines) {
		if (lines.size() < 2) return ""; //$NON-NLS-1$
		
		String commStart = ""; //$NON-NLS-1$
		
		int minlen = getMinLen(lines);

		for (int len = 0; len < minlen; len++) {
			String starter = lines.get(0).substring(0, len);
			
			for (int j = 0; j < lines.size(); j++) {
				if (! lines.get(j).startsWith(starter)) {
					return commStart;
				}
			}
			
			commStart = starter;
		}
		
		return commStart;
	}
	
	private static void removeCommonStartings(List<String> lines) {
		String starter = getCommonStartings(lines);
		
		if (starter != null && ! starter.isEmpty()) {
			for (int i = 0; i < lines.size(); i++) {
				lines.set(i, lines.get(i).substring(starter.length()));
			}
		}
	}
	
	private static String getCommonEndings(List<String> lines) {
		if (lines.size() < 2) return ""; //$NON-NLS-1$
		
		String commEnding = ""; //$NON-NLS-1$
		
		int minlen = getMinLen(lines);

		for (int len = 0; len < minlen; len++) {
			String ender = lines.get(0).substring(lines.get(0).length() - len);
			
			for (int j = 0; j < lines.size(); j++) {
				if (! lines.get(j).endsWith(ender)) {
					return commEnding;
				}
			}
			
			commEnding = ender;
		}
		
		return commEnding;
	}
	
	private static void removeCommonEndings(List<String> lines) {
		String ending = getCommonEndings(lines);
		
		if (ending != null && ! ending.isEmpty()) {
			for (int i = 0; i < lines.size(); i++) {
				lines.set(i, lines.get(i).substring(0, lines.get(i).length() - ending.length()));
			}
		}
	}
	
	private static int cleanUpList(List<String> list) {
		int delLines = 0;

		for (int i = list.size() - 1; i >= 0; i--) {
			if (list.get(i).isEmpty()) {
				list.remove(i);
				delLines++;
			}
		}

		return delLines;
	}
	
	private static void trimList(List<String> list) {
		for (int i = 0; i < list.size(); i++) {
			list.set(i, list.get(i).trim());
		}
	}
	
	private static void removeDynamicCounterStartings(List<String> lines) {
		int prev = Integer.MIN_VALUE;
		
		boolean isCounter = true;
		for (int p = 0; p < lines.size(); p++) {
			String line = lines.get(p);
			
			String counter = "";  //$NON-NLS-1$
			for (int i = 0; i < line.length(); i++) {
				if (Character.isDigit(line.charAt(i))) {
					counter += line.charAt(i);
				} else {
					break;
				}
			}
			boolean succ = true;
			try {
				int value = Integer.parseInt(counter);
				
				if (value > prev) {
					succ = true;
					prev = value;
				} else {
					succ = false;
				}
			} catch (NumberFormatException e) {
				succ = false;
			}
			
			if (! succ) {
				isCounter = false;
				break;
			}
		}
		
		if (isCounter) {
			for (int p = 0; p < lines.size(); p++) {
				String line = lines.get(p);

				int len = 0;
				for (int i = 0; i < line.length(); i++) {
					if (Character.isDigit(line.charAt(i))) {
						len++;
					} else {
						break;
					}
				}
				
				lines.set(p, line.substring(len));
			}
		}
	}
	
	private static void removeStaticCounterStartings(List<String> lines) {
		int minlen = Math.min(getMinLen(lines), 4);
		
		int digitlen = minlen;
		
		for (int len = 0; len < minlen; len++) {
			boolean onlyDigits = true;
			
			for (int p = 0; p < lines.size(); p++) {
				for (int i = 0; i < len; i++) {
					if (! Character.isDigit(lines.get(p).charAt(i))) {
						onlyDigits = false;
					}
				}
			}
			
			if (! onlyDigits) {
				digitlen = len - 1;
				break;
			}
		}
		
		if (digitlen > 0) {
			for (int i = 0; i < lines.size(); i++) {
				lines.set(i, lines.get(i).substring(digitlen));
			}
		}
	}
	
	private static void removeCounterEndings(List<String> lines) {
		int minlen = Math.min(getMinLen(lines), 4);
		
		int digitlen = minlen;
		
		for (int len = 1; len < minlen; len++) {
			boolean onlyDigits = true;
			
			for (int p = 0; p < lines.size(); p++) {
				for (int i = 0; i < len; i++) {
					if (! Character.isDigit(lines.get(p).charAt(lines.get(p).length() - i - 1))) {
						onlyDigits = false;
					}
				}
			}
			
			if (! onlyDigits) {
				digitlen = len - 1;
				break;
			}
		}
		
		if (digitlen > 0) {
			for (int i = 0; i < lines.size(); i++) {
				lines.set(i, lines.get(i).substring(0, lines.get(i).length() - digitlen));
			}
		}
	}
	
	private static void replaceSpaceAlternatives(List<String> lines) {
		boolean hasSpaces = countAppearences(" ", lines) > 0; //$NON-NLS-1$
		
		if (! hasSpaces) {
			int pointCount = countAppearences(".", lines); //$NON-NLS-1$
			boolean hasMultPoints = pointCount > lines.size();
			int underscoreCount = countAppearences("_", lines); //$NON-NLS-1$
			boolean hasMultUnderscores = underscoreCount > lines.size();
			int scorecount = countAppearences("-", lines); //$NON-NLS-1$
			boolean hasMultScores = scorecount > lines.size();
			
			int maxAppearence = Math.max(pointCount, Math.max(underscoreCount, scorecount));
			
			for (int i = 0; i < lines.size(); i++) {
				if (hasMultPoints && pointCount == maxAppearence) {
					lines.set(i, lines.get(i).replace(".", " ")); //$NON-NLS-1$ //$NON-NLS-2$
				}
				
				if (hasMultUnderscores && underscoreCount == maxAppearence) {
					lines.set(i, lines.get(i).replace("_", " ")); //$NON-NLS-1$ //$NON-NLS-2$
				}
				
				if (hasMultScores && scorecount == maxAppearence) {
					lines.set(i, lines.get(i).replace("-", " ")); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
	}
	
	private static int countSubstrings(String str, String substr) {
		int occs = 0;

		int pos = 0;

		while ((pos = str.indexOf(substr, pos + 1)) != -1) {
			occs++;
		}

		return occs;
	}
	
	private static int countAppearences(String search, List<String> lines) {
		int apps = 0;
		
		for (int i = 0; i < lines.size(); i++) {
			apps += countSubstrings(lines.get(i), search);
		}
		
		return apps;
	}
	
	// Jep, Umlauts is correct English
	private static void replaceUmlauts(List<String> lines) {
		for (int i = 0; i < lines.size(); i++) {
			lines.set(i, lines.get(i).replace("oe", "ö")); //$NON-NLS-1$ //$NON-NLS-2$
			lines.set(i, lines.get(i).replace("ae", "ä")); //$NON-NLS-1$ //$NON-NLS-2$
			lines.set(i, lines.get(i).replace("ue", "ü")); //$NON-NLS-1$ //$NON-NLS-2$
			
			lines.set(i, lines.get(i).replace("Oe", "Ö")); //$NON-NLS-1$ //$NON-NLS-2$
			lines.set(i, lines.get(i).replace("Ae", "Ä")); //$NON-NLS-1$ //$NON-NLS-2$
			lines.set(i, lines.get(i).replace("Ue", "Ü")); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private static void removeInformationWordsFromList(List<String> lines) {
		for (int i = 0; i < lines.size(); i++) {
			lines.set(i, removeInformationWords(lines.get(i)));
		}
	}

	private static String removeInformationWords(String line) {
		int oldLen = 0;

		while (line.length() != oldLen) {
			oldLen = line.length();

			for (int i = 0; i < INFORMATION_STRINGS.length; i++) {
				line = line.replaceAll("(?i)" + "(?<= )" + Pattern.quote(INFORMATION_STRINGS[i]) + "(?= )", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				if (line.toLowerCase().startsWith(INFORMATION_STRINGS[i].toLowerCase() + " ")) { //$NON-NLS-1$
					line = line.substring(INFORMATION_STRINGS[i].length());
				}

				if (line.toLowerCase().endsWith(" " + INFORMATION_STRINGS[i].toLowerCase())) { //$NON-NLS-1$
					line = line.substring(0, line.length() - INFORMATION_STRINGS[i].length());
				}
			}
		}

		return line;
	}
	
	private static void removeDoubleSpaces(List<String> lines) {
		for (int l = 0; l < lines.size(); l++) {
			int oldLen = -1;
			while(lines.get(l).length() != oldLen) {
				oldLen = lines.get(l).length();
				lines.set(l, lines.get(l).replace("  ", " ")); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
}
