package de.jClipCorn.util.datatypes;

import de.jClipCorn.util.lambda.*;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;

public class Opt<T> implements IOpt<T> {
	public static final Opt<?> EMPTY = new Opt<>();

	public static final Opt<Boolean> False = new Opt<>(false);
	public static final Opt<Boolean> True  = new Opt<>(true);

	private final T value;
	private final boolean isSet;

	private Opt() {
		this.value = null;
		this.isSet = false;
	}

	private Opt(T v) {
		this.value = v;
		this.isSet = true;
	}

	@SuppressWarnings("unchecked")
	public static<T> Opt<T> empty() {
		return (Opt<T>) EMPTY;
	}

	public static <T> Opt<T> of(T value) {
		return new Opt<>(value);
	}

	public static <T> Opt<T> ofNullable(T value) {
		return (value == null) ? empty() : new Opt<>(value);
	}

	public static <T> Opt<T> cast(Object value, Class<T> cls) {
		if (cls.isInstance(value)) {
			return Opt.of(cls.cast(value));
		} else {
			return Opt.empty();
		}
	}

	public T get() {
		if (!isSet) throw new NoSuchElementException("No value present"); //$NON-NLS-1$
		return value;
	}

	public boolean isPresent() {
		return isSet;
	}

	public boolean isEmpty() {
		return !isSet;
	}

	public void ifPresent(Consumer<? super T> consumer) {
		if (isSet) consumer.accept(value);
	}

	public void ifOrElse(Func1to0<T> consumerPresent, Func0to0 consumerEmpty) {
		if (isSet) consumerPresent.invoke(value); else consumerEmpty.invoke();
	}

	public T orElse(T other) {
		return isSet ? value : other;
	}

	public Opt<T> orElseFlat(Opt<T> other) {
		return isSet ? this : other;
	}

	@SuppressWarnings("unchecked")
	public <TVal> Opt<TVal> flatMap(Func1to1<T, IOpt<TVal>> map) {
		if (!isPresent()) return (Opt<TVal>) EMPTY;
		var v = map.invoke(value);
		if (v.isPresent()) return Opt.of(v.get());
		return (Opt<TVal>) EMPTY;
	}

	@SuppressWarnings("unchecked")
	public <TVal, TErr> ErrOpt<TVal, TErr> flatErrMap(Func1to1<T, ErrOpt<TVal, TErr>> map) {
		if (!isPresent()) return ErrOpt.empty();
		var v = map.invoke(value);
		if (v.isPresent()) return ErrOpt.of(v.get());
		if (v.isError()) return ErrOpt.err(v.getErr());
		return ErrOpt.empty();
	}

	@SuppressWarnings("unchecked")
	public <TVal> Opt<TVal> map(Func1to1<T, TVal> map) {
		if (!isPresent()) return (Opt<TVal>) EMPTY;
		return Opt.of(map.invoke(value));
	}

	public <TVal> TVal mapOrElse(Func1to1<T, TVal> map, TVal falllback) {
		if (!isPresent()) return falllback;
		return map.invoke(value);
	}

	@SuppressWarnings("unchecked")
	public <TVal, TExc extends Throwable> Opt<TVal> mapThrow(Func1to1WithGenericException<T, TVal, TExc> map) throws TExc {
		if (!isPresent()) return (Opt<TVal>) EMPTY;
		return Opt.of(map.invoke(value));
	}

	public Optional<T> toOptional() {
		if (isSet) {
			if (value != null) return Optional.of(value);
			else return Optional.empty();
		} else {
			return Optional.empty();
		}
	}

	public boolean isEqual(Opt<T> other, Func2to1<T, T, Boolean> cmp)
	{
		if (this.isEmpty() && other.isEmpty()) return true;
		if (this.isEmpty() || other.isEmpty()) return false;
		return cmp.invoke(this.get(), other.get());
	}

	public <TErrType> ErrOpt<T, TErrType> toErrOpt() {
		if (this.isEmpty()) return ErrOpt.empty();
		return ErrOpt.of(this.value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object o) {
		if (this == o) return true;

		if (o == null || getClass() != o.getClass()) return false;

		return this.isEqual((Opt<T>) o, Object::equals);
	}

	@Override
	public int hashCode() {
		if (isSet) {
			if (value == null) return 1;
			var hc = value.hashCode();
			return (hc < 0) ? hc : hc + 2;
		} else {
			return 0;
		}
	}
}
