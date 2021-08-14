package de.jClipCorn.features.metadata.exceptions;

public class InnerMediaQueryExceptionWithXML extends InnerMediaQueryException {
	private static final long serialVersionUID = -400882894506601561L;

	public final String XML;

	public InnerMediaQueryExceptionWithXML(InnerMediaQueryException inner, String xml) {
		super(inner.MessageLong, inner);
		XML = xml;
	}
}
