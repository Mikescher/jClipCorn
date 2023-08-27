package de.jClipCorn.util.datatypes;

import java.util.NoSuchElementException;

public class Either3<T1, T2, T3> implements IEither {

	private final T1  value1;
	private final T2  value2;
	private final T3  value3;
	private final int setnum;

	private Either3(T1 v1, T2 v2, T3 v3, int setnum) {
		this.value1 = v1;
		this.value2 = v2;
		this.value3 = v3;
		this.setnum = setnum;
	}

	public static <T1, T2, T3> Either3<T1, T2, T3> V1(T1 v) { return new Either3<>(v, null, null, 1); }
	public static <T1, T2, T3> Either3<T1, T2, T3> V2(T2 v) { return new Either3<>(null, v, null, 2); }
	public static <T1, T2, T3> Either3<T1, T2, T3> V3(T3 v) { return new Either3<>(null, null, v, 3); }

	public int getValueNumber() {
		return setnum;
	}

	public int getValueCount() {
		return 3;
	}

	public T1 get1() {
		if (setnum != 1) throw new NoSuchElementException("Value 1 not present"); //$NON-NLS-1$
		return value1;
	}

	public T2 get2() {
		if (setnum != 2) throw new NoSuchElementException("Value 2 not present"); //$NON-NLS-1$
		return value2;
	}

	public T3 get3() {
		if (setnum != 3) throw new NoSuchElementException("Value 3 not present"); //$NON-NLS-1$
		return value3;
	}

	public T1 get1OrElse(T1 def) {
		if (setnum != 1) return def;
		return value1;
	}

	public T2 get2orElse(T2 def) {
		if (setnum != 2) return def;
		return value2;
	}

	public T3 get3orElse(T3 def) {
		if (setnum != 3) return def;
		return value3;
	}

	public Opt<T1> get1Opt() {
		if (setnum != 1) return Opt.empty();
		return Opt.of(value1);
	}

	public Opt<T2> get2Opt() {
		if (setnum != 2) return Opt.empty();
		return Opt.of(value2);
	}

	public Opt<T3> get3Opt() {
		if (setnum != 3) return Opt.empty();
		return Opt.of(value3);
	}
}
