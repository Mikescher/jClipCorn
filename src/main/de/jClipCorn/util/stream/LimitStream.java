package de.jClipCorn.util.stream;

public class LimitStream<TType> extends CCSimpleStream<TType> {

	private CCStream<TType> source;
	private int count;
	private int position;

	public LimitStream(CCStream<TType> _source, int _count) {
		super();
		source   = _source;
		count    = _count;
		position = 0;
	}

	@Override
	public TType nextOrNothing(boolean first) {
		if (!source.hasNext()) return finishStream();

		position++;
		if (position > count) return finishStream();

		return source.next();
	}

	@Override
	protected CCStream<TType> cloneFresh() {
		return new LimitStream<>(source.cloneFresh(), count);
	}
}
