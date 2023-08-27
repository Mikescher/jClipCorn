package de.jClipCorn.util.datatypes;

import java.util.NoSuchElementException;

public class Either2<T1, T2> implements IEither {

	private final T1  value1;
	private final T2  value2;
	private final int setnum;

	private Either2(T1 v1, T2 v2, int setnum) {
		this.value1 = v1;
		this.value2 = v2;
		this.setnum = setnum;
	}

	public static <T1, T2> Either2<T1, T2> V1(T1 v) { return new Either2<>(v, null, 1); }
	public static <T1, T2> Either2<T1, T2> V2(T2 v) { return new Either2<>(null, v, 2); }

	public int getValueNumber() {
		return setnum;
	}

	public int getValueCount() {
		return 2;
	}

	public T1 get1() {
		if (setnum != 1) throw new NoSuchElementException("Value 1 not present"); //$NON-NLS-1$
		return value1;
	}

	public T2 get2() {
		if (setnum != 2) throw new NoSuchElementException("Value 2 not present"); //$NON-NLS-1$
		return value2;
	}

	public T1 get1OrElse(T1 def) {
		if (setnum != 1) return def;
		return value1;
	}

	public T2 get2orElse(T2 def) {
		if (setnum != 2) return def;
		return value2;
	}

	public Opt<T1> get1Opt() {
		if (setnum != 1) return Opt.empty();
		return Opt.of(value1);
	}

	public Opt<T2> get2Opt() {
		if (setnum != 2) return Opt.empty();
		return Opt.of(value2);
	}
}
