package de.jClipCorn.util;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

public class ByteFormat extends NumberFormat {
	private static final long serialVersionUID = 2310730363073432664L;

	@Override
	public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
		return format((long)number, toAppendTo, pos);
	}

	@Override
	public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
		return toAppendTo.append(FileSizeFormatter.format(number));
	}

	@Override
	public Number parse(String source, ParsePosition parsePosition) {
		return 0; // Not implemented
	}

}
