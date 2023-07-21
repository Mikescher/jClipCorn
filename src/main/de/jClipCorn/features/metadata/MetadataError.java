package de.jClipCorn.features.metadata;

public class MetadataError extends Exception {
	public final String TextShort;
	public final String TextLong;
	public final Throwable Inner;

	public MetadataError(String textShort, String textLong, Throwable inner) {
		TextShort = textShort;
		TextLong  = textLong;
		Inner     = inner;
	}

	public static MetadataError Wrap(Throwable t) {
		return new MetadataError(t.getMessage(), t.getMessage(), t);
	}
}
