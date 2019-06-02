package de.jClipCorn.util.colorquantizer.smartk8;

public class FastRandom {
	private final static Double RealUnitInt = 1.0 / (Integer.MAX_VALUE + 1.0);

	private long x, y, z, w;

	public FastRandom(long seed)
	{
		x = seed;
		y = 842502087L;
		z = 3579807591L;
		w = 273326509L;
	}

	public int Next(int upperBound)
	{
		long t = (x ^ (x << 11));
		x = y;
		y = z;
		z = w;
		return (int) ((RealUnitInt*(int) (0x7FFFFFFF & (w = (w ^ (w >> 19)) ^ (t ^ (t >> 8)))))*upperBound);
	}
}
