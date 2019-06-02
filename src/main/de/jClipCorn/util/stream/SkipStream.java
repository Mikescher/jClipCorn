package de.jClipCorn.util.stream;

public class SkipStream<TType> extends CCSimpleStream<TType> {

	private CCStream<TType> source;
	private int count;

	public SkipStream(CCStream<TType> _source, int _count) {
		super();
		source   = _source;
		count    = _count;
	}

	@Override
	public TType nextOrNothing(boolean first) {
		for (int i = 0; i < count; i++) {
			if (!source.hasNext()) return finishStream();
			source.next();
		}

		if (source.hasNext()) return source.next();

		return finishStream();
	}

	@Override
	protected CCStream<TType> cloneFresh() {
		return new SkipStream<>(source.cloneFresh(), count);
	}
}
