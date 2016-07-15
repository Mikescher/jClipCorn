package de.jClipCorn.util.parser;

import java.util.HashSet;
import java.util.Map;

public class StringSpecParser {

	public static String build(String fmt, StringSpecSupplier supplier) {
		HashSet<Character> specifier = supplier.getAllStringSpecifier();
		
		StringBuilder repbuilder = new StringBuilder();
		char actualCounter = '\0';
		int counter = 0;
		char c;
		
		for (int p = 0;p<fmt.length();p++) {
			c = fmt.charAt(p);
			if (specifier.contains(c)) {
				if (counter == 0 || actualCounter == c) {
					counter++;
					actualCounter = c;
				} else {
					String tmpformat = supplier.resolveStringSpecifier(actualCounter, counter);
					
					if (tmpformat == null) return null;
					
					repbuilder.append(tmpformat);
					
					counter = 1;
					actualCounter = c;
				}
			} else {
				if (counter > 0) {
					String tmpformat = supplier.resolveStringSpecifier(actualCounter, counter);
					
					if (tmpformat == null) return null;
					
					repbuilder.append(tmpformat);
				}
				repbuilder.append(c);
				counter = 0;
				actualCounter = '-';
			}
		}
		
		if (counter > 0) {
			String tmpformat = supplier.resolveStringSpecifier(actualCounter, counter);
			
			if (tmpformat == null) return null;
			
			repbuilder.append(tmpformat);
		}
		
		return repbuilder.toString();
	}
	
	public static Object parse(String rawData, String fmt, StringSpecSupplier supplier) {
		HashSet<Character> specifier = supplier.getAllStringSpecifier();
		
		Map<Character, Integer> values = supplier.getSpecDefaults();
		
		char c;
		int rp = 0;
		
		rawData += '\0';
		
		for (int p = 0;p<fmt.length();p++) {
			c = fmt.charAt(p);
			while((p+1)<fmt.length() && fmt.charAt(p+1) == fmt.charAt(p) && (specifier.contains(c))) {
				p++;
			}
			
			if (specifier.contains(c)) {
				String drep = ""; //$NON-NLS-1$
				
				if (Character.isDigit(rawData.charAt(rp))) {
					drep += rawData.charAt(rp);
					rp++;
				} else {
					return null;
				}
				
				for (; Character.isDigit(rawData.charAt(rp)) ; rp++) {
					drep += rawData.charAt(rp);
				}
				
				values.put(c, Integer.parseInt(drep));
			}  else {
				if (rawData.charAt(rp) == c) {
					rp++;
				} else {
					return null;
				}
			}
		}
		
		return supplier.createFromParsedData(values);
	}
}
