package de.jClipCorn.util.datatypes;

import de.jClipCorn.util.lambda.Func1to1;

import java.util.Objects;

public class CountAppendix<TType> {
	private int count;
	private final Func1to1<CountAppendix<TType>, String> display;
	public final TType Value;

	public CountAppendix(TType v, int initial, Func1to1<CountAppendix<TType>, String> toString) {
		this.Value = v;
		this.count = initial;
		this.display = toString;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CountAppendix<?> that = (CountAppendix<?>) o;
		return count == that.count && Objects.equals(Value, that.Value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(count, Value);
	}

	@Override
	@SuppressWarnings("nls")
	public String toString() {
		if (display != null) return display.invoke(this);

		String s1 = "NULL";
		if (Value != null) s1 = Value.toString();

		return s1 + " (" + count + ")";
	}

	public void incCount() {
		count++;
	}

	public int getCount() {
		return count;
	}
}