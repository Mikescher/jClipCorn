package de.jClipCorn.util.formatter;

public class RomanNumberFormatter {
	private static final String[] RCODE = { "M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$
	private static final int[] BVAL = { 1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1 };
	
	private static final char[] RFCODE = { 'M', 'D', 'C', 'L', 'X', 'V', 'I' };
	private static final int[] BFVAL = { 1000, 500, 100, 50, 10, 5, 1 };

	public static String decToRom(int dec) {
		StringBuilder romanbuilder = new StringBuilder();

		for (int i = 0; i < RCODE.length; i++) {
			while (dec >= BVAL[i]) {
				dec -= BVAL[i];
				romanbuilder.append(RCODE[i]);
			}
		}
		return romanbuilder.toString();
	}

	public static int romToDec(String rom) {
		int decimal = 0;
		int lastNumber = 0;

		for (int x = rom.length() - 1; x >= 0; x--) {
			for (int i = 0; i < RFCODE.length; i++) {
				if (RFCODE[i] == rom.charAt(x)) {
					decimal = (lastNumber > BFVAL[i]) ? (decimal - BFVAL[i]) : (decimal + BFVAL[i]);
					lastNumber = BFVAL[i];
					break;
				}
			}
		}
		return decimal;
	}

	public static boolean isRoman(String rom) {
		if (rom.isEmpty()) {
			return false;
		}
		
		for (int x = rom.length() - 1; x >= 0; x--) {
			switch (rom.charAt(x)) {
			case 'M':
			case 'D':
			case 'C':
			case 'L':
			case 'X':
			case 'V':
			case 'I':
				break;
			default:
				return false;
			}
		}
		return true;
	}
	
	public static boolean endsWithRoman(String s) {
		s = s.substring(s.lastIndexOf(" ") + 1); //$NON-NLS-1$
		
		if (isRoman(s)) {
			return isRoman(s);
		} else {
			return isRoman(s);
		}
	}
}
