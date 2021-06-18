package de.jClipCorn.util.stream;

import de.jClipCorn.util.lambda.Func2to1;
import org.apache.commons.lang3.ObjectUtils;

import java.util.*;

public final class CCStreams {
	private CCStreams() {
		throw new InstantiationError();
	}

	@SuppressWarnings("rawtypes")
	public static CCStream<Object> iterateList(List ls) {
		return new ArrayStream<>(ls.toArray());
	}

	public static <T> CCStream<T> iterate(Iterable<T> ls) {
		return new IterableStream<>(ls);
	}

	public static <T> CCStream<T> iterate(T[] ls) {
		return new ArrayStream<>(ls);
	}

	public static CCStream<Byte> iterate(byte[] ls) {
		return new ByteArrayStream(ls);
	}

	public static CCStream<Double> iterate(double[] ls) {
		return new DoubleArrayStream(ls);
	}

	@SafeVarargs
	public static <T> CCStream<T> iterate(Iterable<T> first, Iterable<T>... rest) {
		CCStream<T> s = iterate(first);
		for (Iterable<T> i : rest) s = s.append(i);
		return s;
	}

	public static <T> CCStream<T> single(T ls) {
		return new SingleStream<>(ls);
	}

	public static <T> CCStream<T> empty() {
		return new EmptyStream<>();
	}

	public static <T> CCStream<T> iterate(Enumeration<T> ls) {
		return iterate(Collections.list(ls));
	}

	public static CCStream<Character> iterate(String str) {
		return new StringStream(str);
	}

	public static CCStream<Integer> countRange(int start, int end) {
		return new CounterStream(start, end - start); // start included | end excluded
	}

	public static <K, V> CCStream<Map.Entry<K, V>> iterate(Map<K, V> map) {
		return new IterableStream<>(map.entrySet());
	}

	public static <V1, V2> boolean equalsElementwiseAuto(CCStream<V1> a, CCStream<V2> b) {
		return equalsElementwise(a, b, (v1, v2) -> autoCompare(v1, v2)==0);
	}

	public static <V1, V2> boolean equalsElementwise(CCStream<V1> a, CCStream<V2> b, Func2to1<V1, V2, Boolean> cmp) {
		if (a == b) return true;

		while (true)
		{
			var na = a.hasNext();
			var nb = b.hasNext();

			if (!na && !nb) return true; // both finished

			if (na != nb) return false; // different length

			V1 va = a.next();
			V2 vb = b.next();

			if (!cmp.invoke(va, vb)) return false; // different element
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static int autoCompare(Object a, Object b) {
		if (a == null && b == null) return 0;
		if (a instanceof Comparable && b instanceof Comparable) return ObjectUtils.compare((Comparable)a, (Comparable)b);
		return Integer.compare(Objects.hashCode(a), Objects.hashCode(b));
	}
}
