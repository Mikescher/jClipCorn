package de.jClipCorn.util.xml;

public class CCXMLException extends Exception {
	private static final long serialVersionUID = 6176290074697319303L;
	
	public final String Message;
	public final String XMLContent;

	public CCXMLException(String message, String xml) {
		super(message);

		Message = message;
		XMLContent = xml;
	}

}
