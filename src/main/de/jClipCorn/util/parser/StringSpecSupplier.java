package de.jClipCorn.util.parser;

import java.util.HashSet;
import java.util.Map;

public interface StringSpecSupplier {
	String resolveStringSpecifier(char c, int count);
	HashSet<Character> getAllStringSpecifier();
	Object createFromParsedData(Map<Character, Integer> values);
	Map<Character, Integer> getSpecDefaults();
}