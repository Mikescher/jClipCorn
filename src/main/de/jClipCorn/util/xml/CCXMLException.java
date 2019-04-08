package de.jClipCorn.util.xml;

public class CCXMLException extends Exception {

	public final String Message;
	public final String XMLContent;

	public CCXMLException(String message, String xml) {
		super(message);

		Message = message;
		XMLContent = xml;
	}

}
