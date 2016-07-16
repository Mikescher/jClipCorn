package de.jClipCorn.util.parser;

import java.util.HashSet;
import java.util.Map;

public interface StringSpecSupplier {
	public String resolveStringSpecifier(char c, int count);
	public HashSet<Character> getAllStringSpecifier();
	public Object createFromParsedData(Map<Character, Integer> values);
	public Map<Character, Integer> getSpecDefaults();
}