package de.jClipCorn.util.stream;

public class DoubleArrayStream extends CCStream<Double> {

	private final double[] arr;
	private int pos = 0;

	public DoubleArrayStream(double[] _arr) {
		super();
		arr = _arr;
	}
	
	@Override
	public boolean hasNext() {
		return pos < arr.length;
	}

	@Override
	public Double next() {
		return arr[pos++];
	}

	@Override
	protected CCStream<Double> cloneFresh() {
		return new DoubleArrayStream(arr);
	}

}
