package de.jClipCorn.util.stream;

import de.jClipCorn.util.datatypes.Tuple;

public class ZipStream<TType1, TType2> extends CCSimpleStream<Tuple<TType1, TType2>> {

	private final CCStream<TType1> stream1;
	private final CCStream<TType2> stream2;

	private final TType1 default1;
	private final TType2 default2;

	private final boolean full; // iterate until both streams are empty

	public ZipStream(CCStream<TType1> s1, CCStream<TType2> s2, TType1 d1, TType2 d2, boolean f) {
		super();

		stream1 = s1;
		stream2 = s2;

		default1 = d1;
		default2 = d2;

		full = f;
	}

	@Override
	public Tuple<TType1, TType2> nextOrNothing(boolean first) {

		int sc = 0;

		TType1 v1 = default1;
		if (stream1.hasNext()) { sc++; v1 = stream1.next(); }

		TType2 v2 = default2;
		if (stream2.hasNext()) { sc++; v2 = stream2.next(); }

		if (sc == 0) return finishStream();

		if (sc == 1 && !full) return finishStream();

		return Tuple.Create(v1, v2);
	}

	@Override
	protected CCStream<Tuple<TType1, TType2>> cloneFresh() {
		return new ZipStream<>(stream1.cloneFresh(), stream2.cloneFresh(), default1, default2, full);
	}
}
