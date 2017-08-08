package de.jClipCorn.util.stream;

import java.util.ArrayList;
import java.util.List;

public class ConcatStream<TType> extends CCSimpleStream<TType> {

	private List<CCStream<TType>> streams;
	private int index;
	
	public ConcatStream(CCStream<TType> _s1, CCStream<TType> _s2) {
		super();
		streams = new ArrayList<>();
		
		streams.add(_s1);
		streams.add(_s2);
		
		index = 0;
	}
	
	@SafeVarargs
	public ConcatStream(CCStream<TType> _s1, CCStream<TType>... _more) {
		super();
		streams = new ArrayList<>();
		
		streams.add(_s1);
		for (CCStream<TType> strm : _more) streams.add(strm);
		
		index = 0;
	}
	
	public ConcatStream(List<CCStream<TType>> _streams) {
		super();
		streams = new ArrayList<>();
		
		streams.addAll(_streams);
		
		index = 0;
	}
	
	@Override
	public TType nextOrNothing(boolean first) {
		if (index >= streams.size()) return finishStream();
		
		for(;;) {
			if (streams.get(index).hasNext()) return streams.get(index).next();
			
			index++;
			if (index >= streams.size()) return finishStream();
		}
	}

	@Override
	protected CCStream<TType> cloneFresh() {
		return new ConcatStream<>(CCStreams.iterate(streams).map(s -> s.cloneFresh()).enumerate());
	}
}
