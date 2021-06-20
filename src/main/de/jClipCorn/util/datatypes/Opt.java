package de.jClipCorn.util.datatypes;

import de.jClipCorn.util.lambda.Func1to1;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;

public class Opt<T> {
	private static final Opt<?> EMPTY = new Opt<>();

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

	public T get() {
		if (!isSet) throw new NoSuchElementException("No value present"); //$NON-NLS-1$
		return value;
	}

	public boolean isPresent() {
		return isSet;
	}

	public void ifPresent(Consumer<? super T> consumer) {
		if (isSet) consumer.accept(value);
	}

	public T orElse(T other) {
		return isSet ? value : other;
	}

	@SuppressWarnings("unchecked")
	public <TVal> Opt<TVal> flatten(Func1to1<T, Opt<TVal>> map) {
		if (!isPresent()) return (Opt<TVal>) EMPTY;
		return map.invoke(value);
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

	public Optional<T> toOptional() {
		if (isSet) {
			if (value != null) return Optional.of(value);
			else return Optional.empty();
		} else {
			return Optional.empty();
		}
	}
}
