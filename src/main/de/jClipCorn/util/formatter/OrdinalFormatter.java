package de.jClipCorn.util.formatter;

public class OrdinalFormatter
{
	@SuppressWarnings("nls")
	public static String formatOrdinal(int v)
	{
		if (v == 0) return "0";
		if (v < 0) return "-" + formatOrdinal(-v);

		var d100 = v % 100;

		if (d100 == 1) return String.format("%dst", v);
		if (d100 == 2) return String.format("%dnd", v);
		if (d100 == 3) return String.format("%drd", v);
		if (d100 == 4) return String.format("%dth", v);

		if (d100 == 10) return String.format("%dth", v);
		if (d100 == 11) return String.format("%dth", v);
		if (d100 == 12) return String.format("%dth", v);
		if (d100 == 13) return String.format("%dth", v);

		var d10 = v % 10;

		if (d10 == 0) return String.format("%dth", v);
		if (d10 == 1) return String.format("%dst", v);
		if (d10 == 2) return String.format("%dnd", v);
		if (d10 == 3) return String.format("%drd", v);
		if (d10 == 4) return String.format("%dth", v);
		if (d10 == 5) return String.format("%dth", v);
		if (d10 == 6) return String.format("%dth", v);
		if (d10 == 7) return String.format("%dth", v);
		if (d10 == 8) return String.format("%dth", v);
		if (d10 == 9) return String.format("%dth", v);

		throw new Error(); // unreachable
	}

}
