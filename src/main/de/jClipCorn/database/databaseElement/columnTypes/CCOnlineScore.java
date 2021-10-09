package de.jClipCorn.database.databaseElement.columnTypes;

import de.jClipCorn.util.Str;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.exceptions.OnlineScoreFormatException;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class CCOnlineScore implements Comparable<CCOnlineScore> {
	public static final CCOnlineScore ZERO_OF_TEN = create((short)0, (short)10);
	public static final CCOnlineScore EMPTY = create((short)0, (short)0);

	public final short Numerator;
	public final short Denominator;

	private CCOnlineScore(short n, short d) {
		Numerator = n;
		Denominator = d;
	}

	public static int compare(CCOnlineScore o1, CCOnlineScore o2) {
		var r = Float.compare(o1.getRatio(), o2.getRatio());
		if (r != 0) return r;
		r = Short.compare(o1.Denominator, o2.Denominator);
		if (r != 0) return r;
		r = Short.compare(o1.Numerator, o2.Numerator);
		return r;
	}

	public static CCOnlineScore create(short n, short d) {
		return new CCOnlineScore(n, d);
	}

	public String toSerializationString() {
		return Numerator+"/"+Denominator;
	}

	public static CCOnlineScore deserialize(String v) throws CCFormatException {
		if (Str.isNullOrEmpty(v)) return ZERO_OF_TEN;
		var arr = v.split("/");
		if (arr.length != 2) throw new OnlineScoreFormatException(v);
		try	{
			var n = Short.parseShort(arr[0]);
			var d = Short.parseShort(arr[1]);
			return create(n, d);
		} catch (NumberFormatException e) {
			throw new OnlineScoreFormatException(v, e);
		}
	}

	public ImageIcon getIcon() {
		return getStars().getIcon();
	}

	public CCOnlineStars getStars() {
		var v = Math.round(getRatio()*10);
		if (v >= 10) v = 10;
		if (v <= 0)  v = 0;
		return CCOnlineStars.getWrapper().findOrFatalError(v);
	}

	public float getRatio() {
		if (Denominator == 0) return 0f;
		if (Numerator <= 0) return 0f;
		if (Numerator >= Denominator) return 1f;
		return (Numerator * 1f) / Denominator;
	}

	public boolean isValid() {
		if (Denominator <= 0) return false;
		if (Numerator > Denominator) return false;
		if (Numerator < 0) return false;
		return true;
	}

	public String getDisplayString() {
		return Numerator + " / " + Denominator;
	}

	public boolean isEmpty() {
		return Numerator == 0 && Denominator == 0;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		return isEqual(this, (CCOnlineScore) o);
	}

	public static boolean isEqual(CCOnlineScore a, CCOnlineScore b)
	{
		if (a == b) return true;
		if (a == null || b == null) return false;
		return (a.Numerator == b.Numerator) && (a.Denominator == b.Denominator);
	}

	@Override
	public int compareTo(@NotNull CCOnlineScore o) {
		return compare(this, o);
	}

	@Override
	public int hashCode() {
		return 31 * Numerator + (int) Denominator;
	}
}
