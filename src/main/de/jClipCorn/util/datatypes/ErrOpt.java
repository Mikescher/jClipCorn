package de.jClipCorn.util.datatypes;

import de.jClipCorn.util.lambda.Func0to1WithGenericException;
import de.jClipCorn.util.lambda.Func1to1;
import de.jClipCorn.util.lambda.Func1to1WithGenericException;
import de.jClipCorn.util.lambda.Func2to1;

import java.util.NoSuchElementException;
import java.util.function.Consumer;

public class ErrOpt<TData, TError> implements IOpt<TData> {
	private static final int MODE_EMPTY = 0;
	private static final int MODE_DATA  = 1;
	private static final int MODE_ERROR = 2;

	public static final ErrOpt<?,?> EMPTY = new ErrOpt<>(MODE_EMPTY, null, null);

	private final TData  valueData;
	private final TError valueErr;
	private final int mode;

	private ErrOpt(int m, TData v1, TError v2) {
		this.valueData = v1;
		this.valueErr  = v2;
		this.mode      = m;
	}

	@SuppressWarnings("unchecked")
	public static<TData, TError> ErrOpt<TData, TError> empty() {
		return (ErrOpt<TData, TError>) EMPTY;
	}

	public static <TData, TError> ErrOpt<TData, TError> of(TData value) {
		return new ErrOpt<>(MODE_DATA, value, null);
	}

	public static <TData, TError, TException extends Throwable> ErrOpt<TData, TError> of(Func0to1WithGenericException<TData, TException> generator, Func1to1<Throwable, TError> errmap) {
		try {
			var v = generator.invoke();
			return new ErrOpt<>(MODE_DATA, v, null);
		} catch (Throwable t) {
			return new ErrOpt<>(MODE_ERROR, null, errmap.invoke(t));
		}
	}

	public static <TData, TError> ErrOpt<TData, TError> ofNullable(TData value) {
		return (value == null) ? empty() : new ErrOpt<>(MODE_DATA, value, null);
	}

	public static <TData, TError> ErrOpt<TData, TError> err(TError value) {
		return new ErrOpt<>(MODE_ERROR, null, value);
	}

	public TData get() {
		if (mode == MODE_ERROR) throw new NoSuchElementException("value is error"); //$NON-NLS-1$
		if (mode == MODE_EMPTY) throw new NoSuchElementException("No value present"); //$NON-NLS-1$
		return valueData;
	}

	public TError getErr() {
		if (mode == MODE_DATA)  throw new NoSuchElementException("value is set"); //$NON-NLS-1$
		if (mode == MODE_EMPTY) throw new NoSuchElementException("No value present"); //$NON-NLS-1$
		return valueErr;
	}

	public boolean isPresent() {
		return (mode == MODE_DATA);
	}

	public boolean isEmpty() {
		return (mode == MODE_EMPTY);
	}

	public boolean isError() {
		return (mode == MODE_ERROR);
	}

	public void ifPresent(Consumer<? super TData> consumer) {
		if (mode == MODE_DATA) consumer.accept(valueData);
	}

	public void ifError(Consumer<? super TError> consumer) {
		if (mode == MODE_ERROR) consumer.accept(valueErr);
	}

	public TData orElse(TData other) {
		return (mode == MODE_DATA) ? valueData : other;
	}

	public TData orElseOrErr(TData otherEmpty, TData otherErr) {
		if (mode == MODE_EMPTY) return otherEmpty;
		if (mode == MODE_ERROR) return otherErr;
		return valueData;
	}

	@SuppressWarnings("unchecked")
	public <TVal> ErrOpt<TVal, TError> map(Func1to1<TData, TVal> map) {
		if (mode == MODE_EMPTY) return (ErrOpt<TVal, TError>) EMPTY;
		if (mode == MODE_ERROR) return new ErrOpt<>(MODE_ERROR, null, valueErr);
		return ErrOpt.of(map.invoke(valueData));
	}

	@SuppressWarnings("unchecked")
	public <TVal, TException extends Throwable> ErrOpt<TVal, TError> map(Func1to1WithGenericException<TData, TVal, TException> map, Func1to1<Throwable, TError> errmap) {
		if (mode == MODE_EMPTY) return (ErrOpt<TVal, TError>) EMPTY;
		if (mode == MODE_ERROR) return new ErrOpt<>(MODE_ERROR, null, valueErr);
		return ErrOpt.of(() -> map.invoke(valueData), errmap);
	}

	public <TVal> TVal mapOrElse(Func1to1<TData, TVal> map, TVal falllbackEmpty, TVal falllbackError) {
		if (mode == MODE_EMPTY) return falllbackEmpty;
		if (mode == MODE_ERROR) return falllbackError;
		return map.invoke(valueData);
	}

	@SuppressWarnings("unchecked")
	public <TVal> ErrOpt<TVal, TError> mapFlat(Func1to1<TData, ErrOpt<TVal, TError>> map) {
		if (mode == MODE_EMPTY) return (ErrOpt<TVal, TError>) EMPTY;
		if (mode == MODE_ERROR) return new ErrOpt<>(MODE_ERROR, null, valueErr);

		var v = map.invoke(valueData);

		if (v.isError()) return v;
		if (v.isEmpty()) return v;

		return ErrOpt.of(v.get());
	}

	public boolean isEqual(ErrOpt<TData, TError> other, Func2to1<TData, TData, Boolean> cmp1, Func2to1<TError, TError, Boolean> cmp2)
	{
		if (this.mode == other.mode && this.mode == MODE_EMPTY) return true;
		if (this.mode == other.mode && this.mode == MODE_DATA)  return cmp1.invoke(this.valueData, other.valueData);
		if (this.mode == other.mode && this.mode == MODE_ERROR) return cmp2.invoke(this.valueErr, other.valueErr);
		return false;
	}

	public Opt<TData> toOpt() {
		if (isPresent()) return Opt.of(valueData);
		return Opt.empty();
	}
}
